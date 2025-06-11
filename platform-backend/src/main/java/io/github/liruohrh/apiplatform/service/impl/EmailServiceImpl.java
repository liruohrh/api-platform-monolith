package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.apicommon.utils.ServletUtils;
import io.github.liruohrh.apiplatform.common.util.EmailUtils;
import io.github.liruohrh.apiplatform.common.util.RequestUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.constant.RedisConstant;
import io.github.liruohrh.apiplatform.service.EmailService;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  /**
   * 因为删除验证码可以异步且不用确认，因此就用RedissonClient
   */
  private final String from;

  private final JavaMailSender mailSender;
  private final CacheManager caffeineCacheManager;

  public EmailServiceImpl(
      JavaMailSender mailSender,
      @Value("${spring.mail.username}") String from,
      CacheManager caffeineCacheManager
  ) {
    this.mailSender = mailSender;
    this.from = from;
    this.caffeineCacheManager = caffeineCacheManager;
  }

  @Override
  public String captcha(String email) {
    //接口保护
    ValueWrapper freezeString = caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_FREEZE)
        .get(email);
    if (freezeString != null) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "邮箱被锁定5小时");
    }
    ValueWrapper countString = caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_RETRY).get(email);
    Integer retryCount = countString == null ? null : (Integer) countString.get();
    if (retryCount != null) {
      retryCount++;
      if (retryCount > CommonConstant.MAX_CALL_CAPTCHA) {
        caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_RETRY).evictIfPresent(email);
        caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_FREEZE).put(email, "");
        HttpServletRequest req = ServletUtils.getRequest();
        log.warn("用户请求邮箱验证码超过{}次，email={}, addr={}，headers={}",
            CommonConstant.MAX_CALL_CAPTCHA, email,
            req.getRemoteAddr(), RequestUtils.getHeaders(req)
        );
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "邮箱被锁定5小时");
      }
      caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_RETRY).put(email,  retryCount);
    } else {
      retryCount = 1;
      caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_RETRY).put(email, retryCount);
    }

    //生成验证码
    String captcha = RandomUtil.randomString(CommonConstant.CAPTCHA_SIZE);
    try {
      mailSender.send(
          EmailUtils.fillEmail(mailSender, from, email,
              "API Platform",
              "你的验证码是： " + captcha)
      );
      caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_CAPTCHA)
          .put(email, captcha);
      return captcha;
    } catch (MessagingException e) {
      log.debug("邮箱发送失败，email={} ", email, e);
      throw new BusinessException("发送邮件验证码失败，请检查邮箱");
    }
  }


  @Override
  public void verifyCaptcha(String email, String captcha) {
    //接口保护
    ValueWrapper freezeString = caffeineCacheManager.getCache(
        RedisConstant.PREFIX_EMAIL_FREEZE).get(email);
    if (freezeString != null) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "邮箱被锁定5小时");
    }

    //验证验证码
    ValueWrapper captchaString = caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_CAPTCHA).get(email);
    if (captchaString == null) {
      throw new BusinessException(Resp.fail(ErrorCode.TIMEOUT, "验证码过期"));
    }
    if (!StrUtil.equals(captcha.toLowerCase(), ((String)captchaString.get()).toLowerCase())) {
      ValueWrapper countString = caffeineCacheManager.getCache(
          RedisConstant.PREFIX_EMAIL_VERIFY_CAPTCHA_RETRY).get(email);
      Integer retryCount = countString == null ? null : (Integer) countString.get();
      if (retryCount != null) {
        retryCount++;
        if (retryCount >= CommonConstant.MAX_CALL_VERIFY_CAPTCHA) {
          caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_VERIFY_CAPTCHA_RETRY).evictIfPresent(email);
          caffeineCacheManager.getCache(
              RedisConstant.PREFIX_EMAIL_FREEZE).put(email, "");
          HttpServletRequest req = ServletUtils.getRequest();;
          log.warn("邮箱验证码错误超过{}次，email={}, addr={}，headers={}",
              CommonConstant.MAX_CALL_VERIFY_CAPTCHA, email,
              req.getRemoteAddr(), RequestUtils.getHeaders(req)
          );
          throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "邮箱被锁定5小时");
        }
        caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_VERIFY_CAPTCHA_RETRY).put(email, retryCount);
      } else {
        retryCount = 1;
        caffeineCacheManager.getCache(
            RedisConstant.PREFIX_EMAIL_VERIFY_CAPTCHA_RETRY).put(email, retryCount);
      }
      throw new ParamException("验证码错误, 还剩"+(CommonConstant.MAX_CALL_VERIFY_CAPTCHA - retryCount) + "次机会");
    }
    //验证码成功
    caffeineCacheManager.getCache(RedisConstant.PREFIX_EMAIL_CAPTCHA)
            .evict(email);
  }
}

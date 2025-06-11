package io.github.liruohrh.apiplatform;

import static org.mockito.Mockito.*;

import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.service.EmailService;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
public class EmailTest {
  @Resource
  EmailService emailService;
  @SpyBean
  JavaMailSender mailSender;
  @BeforeEach
  public void setUp() throws NoSuchFieldException {
    doAnswer(invocation -> {
      // 可以添加断言或验证逻辑
      System.out.println("邮件发送被拦截，未实际发送");
      return null;
    }).when(mailSender).send(any(MimeMessage.class));
  }

  @Test
  public void test() {

    String email = "test1@test.com";
    String captcha = emailService.captcha(email);
    String prefix = ErrorCode.PARAM.getMsg() + ", ";
    try {
      emailService.verifyCaptcha(email, "123456");
    }catch (Exception e){
      Assertions.assertTrue(e instanceof ParamException);
      Assertions.assertEquals(prefix+"验证码错误, 还剩4次机会", e.getMessage());
    }
    try {
      emailService.verifyCaptcha(email, "123456");
    }catch (Exception e){
      Assertions.assertTrue(e instanceof ParamException);
      Assertions.assertEquals(prefix+"验证码错误, 还剩3次机会", e.getMessage());
    }
    try {
      emailService.verifyCaptcha(email, "123456");
    }catch (Exception e){
      Assertions.assertTrue(e instanceof ParamException);
      Assertions.assertEquals(prefix+"验证码错误, 还剩2次机会", e.getMessage());
    }
    try {
      emailService.verifyCaptcha(email, "123456");
    }catch (Exception e){
      Assertions.assertTrue(e instanceof ParamException);
      Assertions.assertEquals(prefix+"验证码错误, 还剩1次机会", e.getMessage());
    }
    try {
      emailService.verifyCaptcha(email, "123456");
    }catch (Exception e){
      Assertions.assertTrue(e instanceof ResponseStatusException);
      Assertions.assertEquals("邮箱被锁定5小时", e.getMessage());
    }
  }
}

package io.github.liruohrh.apiplatform.common.servlet;

import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.utils.SignUtils;
import io.github.liruohrh.apiplatform.config.ApiGatewayProperties;
import io.github.liruohrh.apiplatform.constant.RedisConstant;
import io.github.liruohrh.apiplatform.model.dto.ApiCallInfoDto;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.rpc.service.RpcHttpApiService;
import io.github.liruohrh.apiplatform.rpc.service.RpcUserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ContentCachingResponseWrapper;
@Slf4j
public class ApiCallFilter extends OncePerRequestFilter implements Ordered {

  private RpcUserService rpcUserService;
  private RpcHttpApiService rpcHttpApiService;

  private final ApiGatewayProperties.ReplayAttack replayAttack;
  private final CacheManager cacheManager;

  public ApiCallFilter(
      RpcUserService rpcUserService,
      RpcHttpApiService rpcHttpApiService,
      ApiGatewayProperties apiGatewayProperties,
      CacheManager cacheManager
  ) {
    this.replayAttack = apiGatewayProperties.getReplayAttack();
    this.cacheManager = cacheManager;
    this.rpcUserService = rpcUserService;
    this.rpcHttpApiService = rpcHttpApiService;
  }
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String form = req.getHeader("form");
    if (StringUtils.isNotEmpty(form) && form.equals("api-platform")) {
      filterChain.doFilter(req, response);
      return;
    }

    String appKey = req.getHeader(SignUtils.KEY_APP_KEY);
    String nonce = req.getHeader(SignUtils.KEY_NONCE);
    String timestamp = req.getHeader(SignUtils.KEY_TIMESTAMP);
    String extra = req.getHeader(SignUtils.KEY_EXTRA);
    String sign = req.getHeader(SignUtils.KEY_SIGN);
    if (StringUtils.isAnyEmpty(
        appKey, nonce, timestamp, extra, sign
    )) {
      log.warn(
          "path=[{}], remoteAddr=[{}], reason=[签名头有些是空值], appKey=[{}], nonce=[{}], timestamp=[{}], extra=[{}], sign=[{}]",
          req.getRequestURI(), req.getRemoteAddr(),
          appKey, nonce, timestamp, extra, sign
      );
      throw new ParamException("invalid call request headers");
    }
    //防重放
    //timestamp不能超过现在一定时间
    if (System.currentTimeMillis() - Long.parseLong(timestamp) > replayAttack.getMaxAliveTime()) {
      log.warn("path=[{}], remoteAddr=[{}], reason=[签名时间戳超过{}ms], timestamp=[{}]",
          req.getRequestURI(), req.getRemoteAddr(),
          replayAttack.getMaxAliveTime(),
          timestamp
      );
      throw new ParamException("invalid call request headers");
    }
    //一段时间内，nonce只有一个
    ValueWrapper nonceString = cacheManager.getCache(
        RedisConstant.PREFIX_API_CALL_REPLAY_ATTACK).get(nonce);
    if(nonceString != null){
      log.warn("path=[{}], remoteAddr=[{}], reason=[nonce已存在], nonce=[{}]",
          req.getRequestURI(), req.getRemoteAddr(), nonce
      );
      throw new ParamException("invalid call request headers");
    }
    cacheManager.getCache(RedisConstant.PREFIX_API_CALL_REPLAY_ATTACK)
        .put(nonce, "");

    User caller = rpcUserService.getUserByAppKey(appKey);
    if (caller == null) {
      log.warn("path=[{}], remoteAddr=[{}], reason=[根据appKey找不到用户], appKey=[{}]",
          req.getRequestURI(), req.getRemoteAddr(), appKey
      );
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    String appSecret = caller.getAppSecret();
    if (!SignUtils.verify(
        appKey, appSecret,
        nonce, timestamp, extra, sign
    )) {
      log.warn(
          "path=[{}], remoteAddr=[{}], reason=[错误的sign], sign=[{}], appKey=[{}], userId=[{}]",
          req.getRequestURI(), req.getRemoteAddr(), sign, appKey, caller.getId()
      );
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "非法调用");
    }

    //检查调用权限
    if (req.getMethod() == null) {
      throw new ParamException("请求无方法");
    }
    ApiCallInfoDto apiCallInfo = rpcHttpApiService.getApiCallInfo(
        req.getRequestURI(),
        req.getMethod(),
        caller.getId()
    );
    HttpApi httpApi = apiCallInfo.getHttpApi();
    ApiCall apiCall = apiCallInfo.getApiCall();
    if (httpApi == null) {
      throw new ParamException("API不存在或者没上线");
    }
    if (apiCall == null) {
      throw new BusinessException(ErrorCode.NOT_EXISTS, "未下过订单");
    }
    if (!httpApi.getPrice().equals(0.0)) {
      //检查是否还有剩余次数
      rpcHttpApiService.checkLeftTimes(httpApi.getId(), caller.getId());
    }
    handlerResp(req, response,  filterChain, caller, httpApi);
  }

  /**
   * 处理响应
   */
  public void handlerResp(
      HttpServletRequest req,
      HttpServletResponse resp,
      FilterChain chain,
      User caller,
      HttpApi api
  ) throws ServletException, IOException {
    long start = System.currentTimeMillis();

    ContentCachingResponseWrapper cachingResponseWrapper =
        new ContentCachingResponseWrapper(resp);
    chain.doFilter(req, cachingResponseWrapper);
    cachingResponseWrapper.copyBodyToResponse();

    long end = System.currentTimeMillis();
    long timeConsumingMs = end - start;

    //调用成功，扣费
    rpcHttpApiService.onCallAPI(true, (int) timeConsumingMs,
        api.getPrice().equals(0.0), api.getId(), caller.getId());

    byte[] bodyBytes = cachingResponseWrapper.getContentAsByteArray();
    String bodyString;
    String contentType = cachingResponseWrapper.getContentType();
    if (contentType == null) {
      bodyString = "content-type is null";
    } else if (MediaType.APPLICATION_JSON.getSubtype()
        .equals(MediaType.parseMediaType(contentType).getSubtype())) {
      bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
    } else {
      bodyString = "not json, but " + contentType;
    }

    log.info(
        "API CALL, remoteAddr={}, apiId={}, userId={}, statusCode={}, consumingMs={}, body={}",
        req.getRemoteAddr(),
        api.getId(),
        caller.getId(),
        resp.getStatus(),
        end - start,
        bodyString
    );
  }

  @Override
  public int getOrder() {
    return -2;
  }
}

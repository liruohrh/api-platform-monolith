package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.liruohrh.apiplatform.apicommon.error.APIException;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.common.util.RequestUtils;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.model.entity.ApiCallLog;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.APIStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.OrderStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.service.ApiCallLogService;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpAPICallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HttpAPICallServiceImpl implements HttpAPICallService {

  @Resource
  private HttpApiService apiService;
  @Resource
  private ApiCallService apiCallService;
  @Resource
  private OrderService orderService;
  @Resource
  private ApiCallLogService apiCallLogService;

  private final RestTemplate restTemplate = new RestTemplateBuilder()
      .errorHandler(new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
          return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
        }
      })
      .build();

  /**
   * admin 直接调用，不需要检测权限。
   * appKey、appSecret仍然使用admin自己的。
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public ResponseEntity<byte[]> debugCallAPI(Long apiId, HttpServletRequest req,
      HttpServletResponse resp) {
    User caller = LoginUserHolder.get();

    //检查调用权限
    boolean isAdmin = RoleEnum.ADMIN.is(caller.getRole());
    HttpApi httpApi = apiService.getOne(new LambdaQueryWrapper<HttpApi>()
        .select(HttpApi::getMethod, HttpApi::getProtocol, HttpApi::getDomain, HttpApi::getPath,
            HttpApi::getPrice)
        .eq(HttpApi::getId, apiId)
        //管理员可以随意调用
        .eq(!isAdmin, HttpApi::getStatus,
            APIStatusEnum.LAUNCH.getValue())
    );
    if (httpApi == null) {
      throw new ParamException("API不存在或者没上线");
    }
    //管理员可以随意调用
    if (!isAdmin) {
      ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
          .eq(ApiCall::getApiId, apiId)
          .eq(ApiCall::getCallerId, caller.getId())
      );
      if (apiCall == null) {
        throw new BusinessException(ErrorCode.NOT_EXISTS, "未下过订单");
      }
      if (!httpApi.getPrice().equals(0.0)) {
        //检查是否还有剩余次数
        checkLeftTimes(apiId, caller.getId());
      }
    }

    //call
    String appKey = caller.getAppKey();
    String appSecret = caller.getAppSecret();
    MultiValueMap<String, String> headers = RequestUtils.getHeaders2(req);
    headers.set("appKey", appKey);
    headers.set("appSecret", appSecret);
    headers.set("form", "api-platform");
    String url = httpApi.getProtocol() + "://" + httpApi.getDomain() + httpApi.getPath() +
        (StrUtil.isEmpty(req.getQueryString()) ? "" : "?" + req.getQueryString());
    RequestEntity requestEntity;
    try {
      ServletInputStream inputStream = req.getInputStream();
      if (StrUtil.isEmpty(req.getHeader(HttpHeaders.CONTENT_TYPE))
          && inputStream.available() <= 0) {
        requestEntity = new RequestEntity<>(
            headers,
            HttpMethod.resolve(httpApi.getMethod().toUpperCase()),
            URI.create(url)
        );
      } else {
        requestEntity = new RequestEntity<>(
            IoUtil.readBytes(inputStream),
            headers,
            HttpMethod.resolve(httpApi.getMethod().toUpperCase()),
            URI.create(url)
        );
      }
    } catch (IOException e) {
      log.warn("用户{} debug API[{}], [{}, {}] 时, 读取用户请求失败",
          caller.getId(),
          apiId, httpApi.getMethod(), url
      );
      throw new APIException("read req fail");
    }

    long start = System.currentTimeMillis();
    ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
    long end = System.currentTimeMillis();

    if (!isAdmin) {
      afterCallAPI(
          !responseEntity.getStatusCode().isError(),
          (int) (end - start),
          httpApi.getPrice().equals(0.0),
          apiId, caller.getId()
      );
    }

    log.info(
        "API Debug, remoteAddr={}, apiId={}, userId={}, statusCode={}, consumingMs={}, body={}",
        req.getRemoteAddr(), apiId, caller.getId(),
        responseEntity.getStatusCode(), end - start,
        MediaType.APPLICATION_JSON.getSubtype()
            .equals(responseEntity.getHeaders().getContentType().getSubtype())
            ? new String(responseEntity.getBody(), StandardCharsets.UTF_8)
            : "not json, but " + responseEntity.getHeaders().getContentType()
    );
    return responseEntity;
  }


  /**
   * 调用成功 且 不是免费时，扣费
   * 记录调用日志
   * 更新执行次数
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void afterCallAPI(boolean isSuccess, int timeConsumingMs, Boolean isFreeAPI, Long apiId,
      Long callerId) {
    if (isSuccess && Boolean.FALSE.equals(isFreeAPI)) {
      //不是免费的，记录usage
      ApiCall apiCall = apiCallService.getOne(
          new LambdaQueryWrapper<ApiCall>()
              .eq(ApiCall::getApiId, apiId)
              .eq(ApiCall::getCallerId, callerId)
      );
      if (apiCall.getLeftTimes() < 1) {
        //使用最早购买的订单
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
            .eq(Order::getApiId, apiId)
            .eq(Order::getUserId, callerId)
            .ne(Order::getAmount, 0)
            .in(Order::getStatus, OrderStatusEnum.effect())
            .eq(Order::getIsUsed, false)
            .orderByAsc(Order::getCtime)
            .last("limit 1")
        );
        if (order == null) {
          throw new BusinessException(ErrorCode.HAS_NOT_MORE_TIMES);
        }
        MustUtils.dbSuccess(orderService.update(new LambdaUpdateWrapper<Order>()
            .eq(Order::getId, order.getId())
            .set(Order::getIsUsed, true)));
        MustUtils.dbSuccess(apiCallService.update(new LambdaUpdateWrapper<ApiCall>()
            .eq(ApiCall::getApiId, apiId)
            .eq(ApiCall::getCallerId, callerId)
            .set(ApiCall::getLeftTimes, order.getAmount()-1)
        ));
      } else {
        MustUtils.dbSuccess(apiCallService.update(new LambdaUpdateWrapper<ApiCall>()
            .eq(ApiCall::getApiId, apiId)
            .eq(ApiCall::getCallerId, callerId)
            .setSql("left_times = left_times - 1")
        ));
      }
    }
    MustUtils.dbSuccess(apiCallLogService.save(new ApiCallLog(
        apiId, callerId, isSuccess, timeConsumingMs
    )));
  }


  @Override
  public void checkLeftTimes(Long apiId, Long callerId) {
    ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
        .eq(ApiCall::getApiId, apiId)
        .eq(ApiCall::getCallerId, callerId)
    );
    if (apiCall.getLeftTimes() < 1 && !orderService.exists(new LambdaQueryWrapper<Order>()
        .eq(Order::getApiId, apiId)
        .eq(Order::getUserId, callerId)
        .eq(Order::getIsUsed, false)
        .ne(Order::getAmount, 0)
        .in(Order::getStatus, OrderStatusEnum.effect())
    )) {
      throw new BusinessException(ErrorCode.HAS_NOT_MORE_TIMES);
    }
  }
}

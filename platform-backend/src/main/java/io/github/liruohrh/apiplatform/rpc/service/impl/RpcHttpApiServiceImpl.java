package io.github.liruohrh.apiplatform.rpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.model.dto.ApiCallInfoDto;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.rpc.service.RpcHttpApiService;
import io.github.liruohrh.apiplatform.service.ApiCallLogService;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpAPICallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RpcHttpApiServiceImpl implements RpcHttpApiService {
    private final HttpApiService httpApiService;
    private final ApiCallService apiCallService;
    private final ApiCallLogService apiCallLogService;
    @Resource
    private HttpAPICallService httpAPICallService;

  public RpcHttpApiServiceImpl(
      HttpApiService httpApiService,
      ApiCallService apiCallService,
      ApiCallLogService apiCallLogService
  ) {
    this.httpApiService = httpApiService;
    this.apiCallService = apiCallService;
    this.apiCallLogService = apiCallLogService;
  }

  @Override
  public void checkLeftTimes(Long apiId, Long callerId) {
    httpAPICallService.checkLeftTimes(apiId, callerId);
  }

  public ApiCallInfoDto getApiCallInfo(String path, String method, Long userId){
    HttpApi httpApi = httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
        .eq(HttpApi::getPath, path)
        .eq(HttpApi::getMethod, method)
    );
    if(httpApi == null){
      throw new ParamException("can not find api by path and method");
    }
    return new ApiCallInfoDto(
        httpApi,
        apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
            .eq(ApiCall::getApiId, httpApi.getId())
            .eq(ApiCall::getCallerId, userId)
        )
    );
  }

  @Override
  public void onCallAPI(boolean isSuccess, int timeConsumingMs,  Boolean isFreeAPI,Long apiId, Long callerId) {
    httpAPICallService.afterCallAPI(isSuccess, timeConsumingMs, isFreeAPI, apiId, callerId);
  }
}

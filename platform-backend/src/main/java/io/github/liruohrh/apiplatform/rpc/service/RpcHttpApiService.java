package io.github.liruohrh.apiplatform.rpc.service;


import io.github.liruohrh.apiplatform.model.dto.ApiCallInfoDto;

public interface RpcHttpApiService {

  /**
   * 不用检查api.price是否是0
   * @param apiId
   * @param callerId
   */
   void checkLeftTimes(Long apiId, Long callerId) ;
  ApiCallInfoDto getApiCallInfo(String path, String method, Long userId);

  void onCallAPI(boolean isSuccess, int timeConsumingMs,  Boolean isFreeAPI,Long apiId, Long callerId);
}

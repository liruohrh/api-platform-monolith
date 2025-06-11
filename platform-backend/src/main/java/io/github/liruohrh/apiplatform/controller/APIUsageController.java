package io.github.liruohrh.apiplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/usage")
@RestController
public class APIUsageController {
  @Resource
  private ApiCallService apiCallService;

  @GetMapping
  public Resp<Boolean> isFreeUsed(
      @RequestParam("apiId") Long apiId
  ){
    ApiCall apiCall = apiCallService.getOne(
        new LambdaQueryWrapper<ApiCall>()
            .eq(ApiCall::getApiId, apiId)
            .eq(ApiCall::getCallerId, LoginUserHolder.getUserId())
    );
    return Resp.ok(apiCall != null && apiCall.getFreeUsed());
  }
}

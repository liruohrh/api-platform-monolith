package io.github.liruohrh.apiplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.mapper.ApiCallMapper;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.req.admin.UserApiUsageReq;
import io.github.liruohrh.apiplatform.model.req.admin.api.UserApiUsageUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.admin.UserApiUsageVo;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuth(mustRole = "ADMIN")
@RestController
@RequestMapping("/admin/api/usage")
public class AdminAPIUsageController {

  @Resource
  private ApiCallMapper apiCallMapper;

  @Resource
  private ApiCallService apiCallService;
  @Resource
  private UserService userService;
  @Resource
  private HttpApiService apiService;

  @DeleteMapping("/user/{id}")
  public Resp<Void> deleteUserApiUsage(
      @PathVariable("id") Long id
  ) {
    MustUtils.dbSuccess(apiCallService.removeById(id));
    return Resp.ok();
  }

  @PostMapping("/user/{id}")
  public Resp<Void> updateUserApiUsage(
      @PathVariable("id") Long id,
      @RequestBody UserApiUsageUpdateReq req
  ) {
    ApiCall old = apiCallService.getById(id);
    if (old == null) {
      throw new ParamException("找不到记录");
    }
    ApiCall apiCall = new ApiCall();
    apiCall.setId(id);
    BeanUtils.copyProperties(req, apiCall);
    apiCallService.updateById(apiCall);
    return Resp.ok();
  }


  @GetMapping("/user")
  public Resp<PageResp<UserApiUsageVo>> listUserApiUsage(UserApiUsageReq req) {
    QueryWrapper<ApiCall> qw = new QueryWrapper<>();
    if(Boolean.TRUE.equals(req.getExcludeFreeApi())){
        qw.ne("http_api.price", 0.0);
    }
    LambdaQueryWrapper<ApiCall> queryWrapper = qw.lambda();
    if (StringUtils.isNotBlank(req.getUsername())) {
      User one = userService.getOne(new LambdaQueryWrapper<User>()
          .eq(User::getUsername, req.getUsername()));
      if(one == null){
        return Resp.ok(null);
      }
      queryWrapper
          .eq(ApiCall::getCallerId, one.getId());
    }
    if (StringUtils.isNotBlank(req.getApiName())) {
      HttpApi one = apiService.getOne(new LambdaQueryWrapper<HttpApi>()
          .eq(HttpApi::getName, req.getApiName()));
      if(one == null){
        return Resp.ok(null);
      }
      queryWrapper
          .eq(ApiCall::getApiId, one.getId());
    }
    Page<ApiCall> page = apiCallMapper.listUserApiUsage(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_USER_API_USAGE),
        queryWrapper
    );

    Set<Long> apiIdSet = page.getRecords().stream().map(ApiCall::getApiId)
        .collect(Collectors.toSet());
    Map<Long, HttpApi> apiMap =
        apiIdSet.isEmpty() ? Collections.emptyMap() : apiService.listByIds(apiIdSet)
        .stream()
        .collect(Collectors.toMap(HttpApi::getId, Function.identity()));

    Set<Long> userIdSet = page.getRecords().stream().map(ApiCall::getCallerId)
        .collect(Collectors.toSet());
    Map<Long, User> userMap =
        userIdSet.isEmpty() ? Collections.emptyMap() : userService.listByIds(userIdSet)
            .stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    return Resp.ok(new PageResp<>(
        page.getRecords().stream()
            .map(e->{
              UserApiUsageVo vo = new UserApiUsageVo();
              BeanUtils.copyProperties(e, vo);
              HttpApi api = apiMap.get(e.getApiId());
              if(api != null){
                vo.setApiName(api.getName());
                vo.setIsFreeApi(api.getPrice().equals(0.0));
              }
              User user = userMap.get(e.getCallerId());
              if(user != null){
                vo.setUsername(user.getUsername());
                vo.setUserNickname(user.getNickname());
              }
              return vo;
            })
            .collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize()
    ));
  }
}

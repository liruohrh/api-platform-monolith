package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.mapper.HttpApiMapper;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.enume.APIStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.OrderStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.model.req.admin.api.AdminApiSearchReq;
import io.github.liruohrh.apiplatform.model.req.api.ApiSearchReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.req.api.UserApiSearchReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.ApiCallResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import io.github.liruohrh.apiplatform.model.vo.ApiAndUsageVo;
import io.github.liruohrh.apiplatform.model.vo.ApiSearchVo;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpAPICallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@PreAuth(mustRole = "ADMIN")
@RequestMapping("/http-api")
@RestController
public class APIController {

  @Resource
  private OrderService orderService;
  @Resource
  private HttpAPICallService httpAPICallService;
  @Resource
  private  HttpApiMapper apiMapper;
  private final HttpApiService httpApiService;
  private final ApiCallService apiCallService;
  private final UserService userService;

  public APIController(
      HttpApiService httpApiService,
      ApiCallService apiCallService,
      UserService userService
  ) {
    this.httpApiService = httpApiService;
    this.apiCallService = apiCallService;
    this.userService = userService;
  }

  @PostMapping("/purchased")
  public Resp<PageResp<ApiAndUsageVo>> listUserApiAndUsage(UserApiSearchReq req) {
    QueryWrapper<HttpApi> wrapper = new QueryWrapper<>();
    if( req.getLeftTimes() != null){
      wrapper.orderBy(true, req.getLeftTimes(), "left_times");
    }
    LambdaQueryWrapper<HttpApi> queryWrapper = wrapper.lambda();
    if (Boolean.TRUE.equals(req.getIsFree())) {
      queryWrapper.eq(HttpApi::getPrice, 0.0);
    }
    if(StringUtils.isNotBlank(req.getKey())){
      queryWrapper.like(HttpApi::getName, req.getKey());
      queryWrapper.like(HttpApi::getDescription, req.getKey());
    }

    queryWrapper.orderBy(
        req.getPrice() != null,
        Boolean.TRUE.equals(req.getPrice()),
        HttpApi::getPrice
    );

    Long userId = LoginUserHolder.getUserId();
    Page<ApiAndUsageVo> pageResult = apiMapper.listApiUsage(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_API_SEARCH),
        queryWrapper,
        userId
    );
    if(pageResult.getRecords().isEmpty()){
      return PageResp.empty(pageResult);
    }
    Set<Long> apiIdSet = pageResult.getRecords().stream().map(ApiAndUsageVo::getId)
        .collect(Collectors.toSet());

    Map<Long, Integer> apiUsageMap = orderService.list(
            new LambdaQueryWrapper<Order>()
                .select(Order::getId,Order::getApiId, Order::getAmount)
                .eq(Order::getIsUsed, false)
                .ne(Order::getAmount, 0)
                .eq(Order::getUserId, userId)
                .in(Order::getApiId, apiIdSet)
                .in(Order::getStatus, OrderStatusEnum.effect())
        ).stream()
        .collect(Collectors.groupingBy(Order::getApiId, Collectors.summingInt(Order::getAmount)));

    PageResp<ApiAndUsageVo> pageResp = new PageResp<>();
    pageResp.setData(pageResult.getRecords().stream()
            .peek(e->{
              Integer leftTimes = apiUsageMap.get(e.getId());
              if(leftTimes != null){
                e.setLeftTimes(e.getLeftTimes() + leftTimes);
              }
            })
        .collect(Collectors.toList()));
    pageResp.setPages(pageResult.getPages());
    pageResp.setTotal(pageResult.getTotal());
    pageResp.setCurrent(pageResult.getCurrent());
    pageResp.setSize(pageResult.getSize());
    return Resp.ok(pageResp);
  }

  @GetMapping("/search")
  public Resp<PageResp<ApiSearchVo>> searchAPI(ApiSearchReq req) {
    LambdaQueryWrapper<HttpApi> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(HttpApi::getStatus, APIStatusEnum.LAUNCH.getValue());

    if (Boolean.TRUE.equals(req.getIsFree())) {
      queryWrapper.eq(HttpApi::getPrice, 0.0);
    }
    if(StringUtils.isNotBlank(req.getKey())){
      queryWrapper.and(qw->qw
          .like(HttpApi::getName, req.getKey())
          .or()
          .like(HttpApi::getDescription, req.getKey())
      );
    }

    queryWrapper.orderBy(
        req.getPrice() != null,
        Boolean.TRUE.equals(req.getPrice()),
        HttpApi::getPrice
    ).orderBy(
        req.getOrderVolume() != null,
        Boolean.TRUE.equals(req.getOrderVolume()),
        HttpApi::getOrderVolume
    ).orderBy(
        req.getScore() != null,
        Boolean.TRUE.equals(req.getScore()),
        HttpApi::getScore
    ).orderBy(
        req.getCtime() != null,
        Boolean.TRUE.equals(req.getCtime()),
        HttpApi::getCtime
    );

    if (ObjectUtils.allNull(
        req.getPrice(),req.getOrderVolume(),req.getScore(),req.getCtime()
    )) {
       queryWrapper.orderByDesc(HttpApi::getCtime);
    }

    Page<HttpApi> pageResult = httpApiService.page(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_API_SEARCH),
        queryWrapper
    );
    PageResp<ApiSearchVo> pageResp = new PageResp<>();
    pageResp.setData(pageResult.getRecords().stream()
        .map(e->BeanUtil.copyProperties(e, ApiSearchVo.class))
        .collect(Collectors.toList())
    );
    pageResp.setPages(pageResult.getPages());
    pageResp.setTotal(pageResult.getTotal());
    pageResp.setCurrent(pageResult.getCurrent());
    pageResp.setSize(pageResult.getSize());
    return Resp.ok(pageResp);
  }


  /**
   * 分情况
   * 1. 用户可以上传：按用户名前缀
   * 这种情况下，用户需要自己上传token，自己验证token
   * 2. 仅管理员上传，直接转发 （当前模式）
   * 3. /demo 路径，在本项目中
   */
  @GetMapping("/debug/{apiId}")
  public Resp<ApiCallResp> callAPI(@PathVariable("apiId") Long apiId, HttpServletRequest req,
      HttpServletResponse resp) {

    ResponseEntity<byte[]> responseEntity = httpAPICallService.debugCallAPI(apiId, req, resp);
    ApiCallResp apiCallResp = new ApiCallResp();
    if (ArrayUtil.isNotEmpty(responseEntity.getBody())) {
      apiCallResp.setBody(Base64.getEncoder().encodeToString(responseEntity.getBody()));
    }
    apiCallResp.setStatus(responseEntity.getStatusCodeValue());
    apiCallResp.setHeaders(responseEntity.getHeaders());
    return Resp.ok(apiCallResp);
  }

  @GetMapping("/{apiId}")
  public Resp<HttpApiResp> getAPIById(@PathVariable("apiId") Long apiId) {
    return Resp.ok(BeanUtil.copyProperties(
            httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
                .eq(HttpApi::getId, apiId)
                .eq(LoginUserHolder.getUserId() == null || !LoginUserHolder.get().getRole().equals(
                        RoleEnum.ADMIN.getValue()
                    ),
                    HttpApi::getStatus, APIStatusEnum.LAUNCH.getValue())
            ),
            HttpApiResp.class
        )
    );
  }

  @PostMapping("/all")
  public Resp<List<HttpApiResp>> listAllAPI() {
    return Resp.ok(httpApiService.list().stream()
        .map(e -> {
          HttpApiResp httpApiResp = new HttpApiResp();
          BeanUtils.copyProperties(e, httpApiResp);
          return httpApiResp;
        }).collect(Collectors.toList()));
  }

  @PostMapping("/list")
  public Resp<PageResp<HttpApiResp>> listAPI(AdminApiSearchReq req) {
    LambdaQueryWrapper<HttpApi> queryWrapper = new LambdaQueryWrapper<>();

    if(req.getId() != null){
      return Resp.ok(PageResp.one(
          BeanUtil.copyProperties(
              httpApiService.getById(req.getId()),
              HttpApiResp.class
          )
      ));
    }
    if(StringUtils.isNotBlank(req.getName())){
      queryWrapper.like(HttpApi::getName, req.getName());
    }
    if(StringUtils.isNotBlank(req.getDescription())){
      queryWrapper.like(HttpApi::getDescription, req.getDescription());
    }
    if(StringUtils.isNotBlank(req.getMethod())){
      queryWrapper.like(HttpApi::getMethod, req.getMethod());
    }
    if(StringUtils.isNotBlank(req.getDomain())){
      queryWrapper.like(HttpApi::getDomain, req.getDomain());
    }
    if(StringUtils.isNotBlank(req.getPath())){
      queryWrapper.like(HttpApi::getPath, req.getPath());
    }
    if(StringUtils.isNotBlank(req.getProtocol())){
      queryWrapper.like(HttpApi::getProtocol, req.getProtocol());
    }
    if(req.getStatus() != null){
      queryWrapper.like(HttpApi::getStatus, req.getStatus());
    }
    if(req.getCtime() != null && req.getCtime().size() == 2){
      if(req.getCtime().get(0).equals(req.getCtime().get(1))){
        queryWrapper.eq(HttpApi::getCtime, req.getCtime().get(0));
      }else{
        queryWrapper.between(HttpApi::getCtime, req.getCtime().get(0), req.getCtime().get(1));
      }
    }
    if(req.getUtime() != null && req.getUtime().size() == 2){
      if(req.getUtime().get(0).equals(req.getUtime().get(1))){
        queryWrapper.eq(HttpApi::getUtime, req.getUtime().get(0));
      }else{
        queryWrapper.between(HttpApi::getUtime, req.getUtime().get(0), req.getUtime().get(1));
      }
    }
    queryWrapper.orderBy(
        req.getScore() != null,
        Boolean.TRUE.equals(req.getScore()),
        HttpApi::getScore
    ).orderBy(
        req.getOrderVolume() != null,
        Boolean.TRUE.equals(req.getOrderVolume()),
        HttpApi::getOrderVolume
    ).orderBy(
        req.getCtimeS() != null,
        Boolean.TRUE.equals(req.getCtimeS()),
        HttpApi::getCtime
    ).orderBy(
        req.getUtimeS() != null,
        Boolean.TRUE.equals(req.getUtimeS()),
        HttpApi::getUtime
    );

    if (ObjectUtils.allNull(
        req.getScore(),req.getOrderVolume(),req.getCtimeS(),req.getUtimeS()
    )) {
      queryWrapper.orderByDesc(HttpApi::getCtime);
    }

    Page<HttpApi> pageResult = httpApiService.page(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_API),
        queryWrapper
    );
    PageResp<HttpApiResp> pageResp = new PageResp<>();
    pageResp.setData(pageResult.getRecords().stream()
        .map(e->BeanUtil.copyProperties(e, HttpApiResp.class))
        .collect(Collectors.toList())
    );
    pageResp.setPages(pageResult.getPages());
    pageResp.setTotal(pageResult.getTotal());
    pageResp.setCurrent(pageResult.getCurrent());
    pageResp.setSize(pageResult.getSize());
    return Resp.ok(pageResp);
  }

  @PostMapping
  public Resp<Long> addAPI(@RequestBody HttpApiAddReq httpApiAddReq) {
    return Resp.ok(httpApiService.addAPI(httpApiAddReq));
  }

  @PostMapping("/{apiId}/launch")
  public Resp<Void> launchAPI(@PathVariable("apiId") Long apiId) {
    httpApiService.launchAPI(apiId);
    return Resp.ok(null);
  }

  @PostMapping("/{apiId}/roll-off")
  public Resp<Void> rollOffAPI(@PathVariable("apiId") Long apiId) {
    httpApiService.rollOffAPI(apiId);
    return Resp.ok(null);
  }

  @PutMapping
  public Resp<Void> updateAPI(@RequestBody HttpApiUpdateReq httpApiUpdateReq) {
    httpApiService.updateAPI(httpApiUpdateReq);
    return Resp.ok(null);
  }

  @DeleteMapping("/{apiId}")
  public Resp<Void> deleteAPI(@PathVariable("apiId") Long apiId) {
    httpApiService.deleteAPI(apiId);
    return Resp.ok(null);
  }
}

package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.model.entity.Application;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.ApplicationAuditStatus;
import io.github.liruohrh.apiplatform.model.enume.ApplicationType;
import io.github.liruohrh.apiplatform.model.enume.OrderStatusEnum;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderCreateReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderRefundReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSearchReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSortReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import io.github.liruohrh.apiplatform.model.vo.OrderVo;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.ApplicationService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
@Validated
public class OrderController {

  private final Snowflake snowflake = IdUtil.getSnowflake(0, 0);

  @Resource
  private UserService userService;
  @Resource
  private ApplicationService applicationService;
  private final OrderService orderService;
  private final HttpApiService httpApiService;
  private final ApiCallService apiCallService;

  public OrderController(
      OrderService orderService,
      HttpApiService httpApiService,
      ApiCallService apiCallService
  ) {
    this.orderService = orderService;
    this.httpApiService = httpApiService;
    this.apiCallService = apiCallService;
  }


  @Transactional(rollbackFor = Exception.class)
  @DeleteMapping("/refund/cancel")
  public Resp<Void> cancelRefund(@RequestParam("orderUid") String orderUid) {
    Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
        .eq(Order::getOrderId, orderUid));
    if(!OrderStatusEnum.REFUNDING.is(order.getStatus())){
      throw new BusinessException("订单未处于退款状态");
    }
    MustUtils.dbSuccess(orderService.update(new LambdaUpdateWrapper<Order>()
        .eq(Order::getOrderId, orderUid)
        .set(Order::getStatus, OrderStatusEnum.REFUND_CANCEL.getValue())
    ));

    MustUtils.dbSuccess(applicationService.update(new LambdaUpdateWrapper<Application>()
        .eq(Application::getApplicationType, ApplicationType.ORDER_FOUND.getValue())
        .eq(Application::getExtraData, orderUid)
        .set(Application::getAuditStatus, ApplicationAuditStatus.CANCEL.getValue())
    ));
    return Resp.ok();
  }


  @Transactional(rollbackFor = Exception.class)
  @PutMapping("/refund")
  public Resp<Void> refund(@RequestBody OrderRefundReq req) {
    Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
        .eq(Order::getOrderId, req.getOrderId()));
    if (order == null) {
      throw new ParamException("订单不存在");
    }
    if(OrderStatusEnum.REFUND_FAIL.is(order.getStatus())){
      throw new BusinessException("订单退款失败，无法再次申请退款");
    }
    if (!OrderStatusEnum.PAID.is(order.getStatus()) && !OrderStatusEnum.REFUND_CANCEL.is(order.getStatus())) {
      throw new BusinessException("订单未支付，无法退款");
    }
    if (order.getActualPayment().equals(0.0)) {
      throw new BusinessException("订单金额为0，无法退款（免费次数或者其他）");
    }
    HttpApi api = httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
        .eq(HttpApi::getId, order.getApiId())
    );
    if (api.getPrice().equals(0.0)) {
      throw new BusinessException("免费API，无法退款");
    }
    if (order.getIsUsed()) {
      throw new BusinessException("订单已使用，无法退款");
    }
    MustUtils.dbSuccess(orderService.update(new LambdaUpdateWrapper<Order>()
        .eq(Order::getOrderId, req.getOrderId())
        .set(Order::getStatus, OrderStatusEnum.REFUNDING.getValue())
    ));
    Application application = applicationService.getOne(new LambdaQueryWrapper<Application>()
        .eq(Application::getApplicationType, ApplicationType.ORDER_FOUND.getValue())
        .eq(Application::getExtraData, req.getOrderId())
    );
    if(application == null){
      application = new Application();
    }else {
      application.setCtime(Instant.now().toEpochMilli());
    }
    application.setApplicationType(ApplicationType.ORDER_FOUND.getValue());
    application.setAuditStatus(ApplicationAuditStatus.PENDING_AUDIT.getValue());

    application.setReporterId(LoginUserHolder.getUserId());
    application.setExtraData(req.getOrderId());
    application.setReason(req.getReason());
    application.setDescription(StringUtils.isBlank(req.getDescription()) ? "" : req.getDescription());
    applicationService.saveOrUpdate(application);
    return Resp.ok();
  }


  @GetMapping
  public Resp<OrderVo> getOrderById(@RequestParam("orderId") String orderId) {
    OrderVo orderVo = BeanUtil.copyProperties(orderService.getOne(new LambdaQueryWrapper<Order>()
        .eq(Order::getOrderId, orderId)), OrderVo.class);
    orderVo.setApi(
        BeanUtil.copyProperties(httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
                .select(HttpApi::getId, HttpApi::getName, HttpApi::getDescription, HttpApi::getLogoUrl)
            .eq(HttpApi::getId, orderVo.getApiId())), HttpApiResp.class));
    User user = userService.getById(orderVo.getUserId());
    orderVo.setUsername(user.getUsername());
    orderVo.setUserNickname(user.getNickname());

    Application reFundApplication = applicationService.getOne(new LambdaQueryWrapper<Application>()
            .eq(Application::getApplicationType, ApplicationType.ORDER_FOUND.getValue())
        .eq(Application::getExtraData, orderVo.getOrderId()));
    if(reFundApplication != null){
      orderVo.setApplication(reFundApplication);
    }
    return Resp.ok(orderVo);
  }

  @PostMapping("/list")
  public Resp<PageResp<OrderVo>> listOrder(
      @RequestBody PageReq<OrderSearchReq, OrderSortReq> pageReq) {
    return _listOrder(pageReq, LoginUserHolder.getUserId());
  }

  @PreAuth(mustRole = "ADMIN")
  @PostMapping("/admin/list")
  public Resp<PageResp<OrderVo>> listAllOrder(
      @RequestBody PageReq<OrderSearchReq, OrderSortReq> pageReq) {
    return _listOrder(pageReq,
        pageReq.getSearch() != null && pageReq.getSearch().getUserId() != null ? pageReq.getSearch()
            .getUserId() : null);
  }

  private Resp<PageResp<OrderVo>> _listOrder(PageReq<OrderSearchReq, OrderSortReq> pageReq,
      Long userId) {
    Page<OrderVo> page = orderService.listOrder(pageReq, userId);
    if (page.getRecords().isEmpty()) {
      return PageResp.empty(page);
    }
    Map<Long, User> userMap = userService.list(new LambdaQueryWrapper<User>()
            .in(User::getId, page.getRecords().stream()
                .map(OrderVo::getUserId)
                .collect(Collectors.toSet())))
        .stream().collect(Collectors.toMap(User::getId, Function.identity()));

    Map<Long, HttpApi> apiMap = httpApiService.list(new LambdaQueryWrapper<HttpApi>()
            .select(HttpApi::getId, HttpApi::getName)
            .in(HttpApi::getId, page.getRecords().stream()
                .map(OrderVo::getApiId)
                .collect(Collectors.toSet())))
        .stream().collect(Collectors.toMap(HttpApi::getId, Function.identity()));

    Set<String> refundOrderUidSet = page.getRecords().stream()
        .filter(
            e -> OrderStatusEnum.REFUND_FAIL.is(e.getStatus()) || OrderStatusEnum.REFUNDING.is(e.getStatus()) || OrderStatusEnum.REFUND_SUCCESS.is(
                e.getStatus()))
        .map(OrderVo::getOrderId)
        .collect(Collectors.toSet());

    Map<String, Application> refundApplicationMap =
        refundOrderUidSet.isEmpty()
            ? Collections.emptyMap()
            :
                applicationService.list(
                        new LambdaQueryWrapper<Application>()
                            .eq(Application::getApplicationType, ApplicationType.ORDER_FOUND.getValue())
                            .in(Application::getExtraData, refundOrderUidSet)
                    ).stream()
                    .collect(Collectors.toMap(Application::getExtraData, Function.identity()));

    return Resp.ok(new PageResp<>(
        page.getRecords().stream().peek(e -> {
              HttpApi httpApi = apiMap.get(e.getApiId());
              e.setApiName(httpApi == null ? "api被删除" :httpApi.getName());
              User user = userMap.get(e.getUserId());
              e.setUsername(user.getUsername());
              e.setUserNickname(user.getNickname());
              Application refundApplication = refundApplicationMap.get(e.getOrderId());
              if (refundApplication != null) {
                e.setApplication(refundApplication);
              }
            })
            .collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize()
    ));
  }

  /**
   * 伪支付接口。正常应该等待第三方通知结果。
   *
   * @param isPay    表示用户进行支付操作
   * @param isCancel 表示用户取消订单
   */
  @Transactional(rollbackFor = Exception.class)
  @PutMapping
  public Resp<Void> optForOrder(
      @RequestParam("orderId") String orderId,
      @RequestParam(value = "isPay", required = false) Boolean isPay,
      @RequestParam(value = "isCancel", required = false) Boolean isCancel
  ) {
    if (ObjUtil.isAllEmpty(isPay, isCancel)
        || (Boolean.FALSE.equals(isPay) && Boolean.FALSE.equals(isCancel))
    ) {
      throw new ParamException("支付还是取消订单?");
    }
    Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
        .eq(Order::getOrderId, orderId)
    );
    if (order == null) {
      throw new ParamException("订单不存在");
    }

    //更新订单状态
    Long callerId = LoginUserHolder.getUserId();
    LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<Order>()
        .eq(Order::getOrderId, orderId)
        .eq(Order::getUserId, callerId);

    //取消订单
    if (Boolean.TRUE.equals(isCancel)) {
      updateWrapper.set(Order::getStatus, OrderStatusEnum.CANCEL.getValue());
      MustUtils.dbSuccess(orderService.update(updateWrapper));
      return Resp.ok(null);
    }

    //支付订单
    updateWrapper.set(Order::getStatus, OrderStatusEnum.PAID.getValue());
    updateWrapper.set(Order::getActualPayment, order.getAmount() * order.getPrice());
    MustUtils.dbSuccess(orderService.update(updateWrapper));

    Long apiId = order.getApiId();

    MustUtils.dbSuccess(httpApiService.update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, apiId)
        .set(HttpApi::getOrderVolume, httpApiService.getById(apiId).getOrderVolume() + 1)
    ));

//    ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
//        .eq(ApiCall::getApiId, apiId)
//        .eq(ApiCall::getCallerId, callerId)
//    );
//    MustUtils.dbSuccess(apiCallService.update(new LambdaUpdateWrapper<ApiCall>()
//        .eq(ApiCall::getApiId, apiId)
//        .eq(ApiCall::getCallerId, callerId)
//        .set(ApiCall::getLeftTimes, apiCall.getLeftTimes() + order.getAmount())
//    ));
    return Resp.ok(null);
  }


  /**
   * 一定创建了 ApiCall
   */
  @Transactional(rollbackFor = Exception.class)
  @PostMapping
  public Resp<OrderVo> createOrder(
      @Validated @RequestBody OrderCreateReq orderCreateReq) {
    Long apiId = orderCreateReq.getApiId();
    HttpApi httpApi = httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
        .select(HttpApi::getId, HttpApi::getPrice, HttpApi::getFreeTimes, HttpApi::getOrderVolume)
        .eq(HttpApi::getId, apiId)
    );
    if(httpApi == null){
      throw new ParamException("api不存在");
    }
    ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
        .eq(ApiCall::getApiId, httpApi.getId())
        .eq(ApiCall::getCallerId, LoginUserHolder.getUserId())
    );
    Long userId = LoginUserHolder.getUserId();

    //1. 购买免费接口，创建一个ApiCall，直接完成Order
    Double price = httpApi.getPrice();
    if (price.equals(0.0)) {
      if (apiCall != null && apiCall.getFreeUsed()) {
        //第二次购买免费接口，抛出异常
        throw new ParamException("不能重复购买免费接口");
      }
      ApiCall newApiCall = new ApiCall();
      newApiCall.setApiId(apiId);
      newApiCall.setCallerId(userId);
      newApiCall.setFreeUsed(true);
      newApiCall.setLeftTimes(0);
      apiCallService.save(newApiCall);
      Order order = createFreeOrder(apiId, userId);
      orderService.save(order);

      MustUtils.dbSuccess(httpApiService.update(new LambdaUpdateWrapper<HttpApi>()
          .eq(HttpApi::getId, apiId)
          .set(HttpApi::getOrderVolume, httpApi.getOrderVolume() + 1)
      ));
      return Resp.ok(BeanUtil.copyProperties(orderService.getById(order.getId()), OrderVo.class));
    }

    //2. 购买免费次数，也不需要手动确认支付
    if (Boolean.TRUE.equals(orderCreateReq.getFree())) {
      if (apiCall == null) {
        //未购买免费次数 或者 未付费购买
        ApiCall newApiCall = new ApiCall();
        newApiCall.setApiId(apiId);
        newApiCall.setCallerId(userId);
        newApiCall.setFreeUsed(true);
        //免费次数直接设置，因为无法退款
        newApiCall.setLeftTimes(httpApi.getFreeTimes());
        apiCallService.save(newApiCall);
      } else if (apiCall.getFreeUsed()) {
        //只能购买过1次免费次数
        throw new ParamException("只能购买一次免费次数");
      } else {
        //免费变付费了，apiCall就是不为空且FreeUsed=false
        apiCall.setFreeUsed(true);
        apiCall.setLeftTimes(apiCall.getLeftTimes() + httpApi.getFreeTimes());
        apiCallService.saveOrUpdate(apiCall);
      }
      Order order = createFreeUsedOrder(httpApi, userId);
      orderService.save(order);

      MustUtils.dbSuccess(httpApiService.update(new LambdaUpdateWrapper<HttpApi>()
          .eq(HttpApi::getId, apiId)
          .set(HttpApi::getOrderVolume, httpApi.getOrderVolume() + 1)
      ));
      return Resp.ok(BeanUtil.copyProperties(orderService.getById(order.getId()), OrderVo.class));
    }

    //3. 付费购买，保存后，等待完成订单时再添加次数
    Integer amount = orderCreateReq.getAmount();
    int stepAmount = 1;
    if (price <= 0.00009) {
      stepAmount =  1000;
    } else if (price <= 0.0009) {
      stepAmount = 100;
    } else if (price <= 0.009) {
      stepAmount = 10;
    }
    if(amount % stepAmount != 0) {
      throw new ParamException("购买数量必须是" + stepAmount + "的倍数，请求的次数是" + amount);
    }
    if (apiCall == null) {
      apiCall = new ApiCall();
      apiCall.setApiId(apiId);
      apiCall.setCallerId(userId);
      apiCall.setFreeUsed(false);
      apiCall.setLeftTimes(0);
      apiCallService.save(apiCall);
    }
    Order order = createInitOrder(httpApi, userId, amount);
    orderService.save(order);
    return Resp.ok(BeanUtil.copyProperties(orderService.getById(order.getId()), OrderVo.class));
  }

  private Order createInitOrder(HttpApi api, Long userId, Integer amount) {
    Order order = new Order();
    order.setApiId(api.getId());
    order.setUserId(userId);
    order.setStatus(OrderStatusEnum.WAIT_PAY.getValue());
    order.setActualPayment(0.0);
    order.setAmount(amount);
    order.setPrice(api.getPrice());
    order.setOrderId(snowflake.nextIdStr());
    order.setIsUsed(false);
    return order;
  }

  private Order createFreeUsedOrder(HttpApi api, Long userId) {
    Order order = new Order();
    order.setApiId(api.getId());
    order.setUserId(userId);
    order.setStatus(OrderStatusEnum.PAID.getValue());
    order.setActualPayment(0.0);
    order.setAmount(api.getFreeTimes());
    order.setPrice(api.getPrice());
    order.setOrderId(snowflake.nextIdStr());
    order.setIsUsed(true);
    return order;
  }

  private Order createFreeOrder(Long apiId, Long userId) {
    Order order = new Order();
    order.setApiId(apiId);
    order.setUserId(userId);
    order.setStatus(OrderStatusEnum.PAID.getValue());
    order.setActualPayment(0.0);
    order.setAmount(0);
    order.setPrice(0.0);
    order.setOrderId(snowflake.nextIdStr());
    order.setIsUsed(true);
    return order;
  }
}

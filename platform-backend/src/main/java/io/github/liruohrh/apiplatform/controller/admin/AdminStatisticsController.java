package io.github.liruohrh.apiplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.entity.ApiStatus;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.OrderStatusEnum;
import io.github.liruohrh.apiplatform.model.req.ApiOrderStatisticsReq;
import io.github.liruohrh.apiplatform.model.req.ApiUsageReq;
import io.github.liruohrh.apiplatform.model.vo.ApiOrderStatisticsResp;
import io.github.liruohrh.apiplatform.model.vo.ApiOrderStatisticsVo;
import io.github.liruohrh.apiplatform.model.vo.ApiStatusVo;
import io.github.liruohrh.apiplatform.service.ApiCallLogService;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.ApiStatusService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin/statistics")
@RestController
public class AdminStatisticsController {
  @Resource
  private UserService userService;
  @Resource
  private ApiStatusService apiStatusService;
  @Resource
  private OrderService orderService;
  @Resource
  private HttpApiService apiService;
  @Resource
  private ApiCallLogService apiCallLogService;

  @Resource
  private ApiCallService apiCallService;


//  @GetMapping("/order/transaction")
//  public Resp<List<OrderTransactionVo>> listOrderTransactions(
//      OrderTransactionFilters req
//  ) {
//    LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
//     if (StringUtils.isBlank(req.getSearchType()) && StringUtils.isNotBlank(req.getDate())) {
//      LocalDate date = null;
//      LocalDate date2 = null;
//      try {
//        String[] split = req.getDate().split("-");
//        if (split.length == 1) {
//          date = LocalDate.of(
//              Integer.parseInt(split[0]),
//              1,
//              1
//          );
//          date2 = date.plusYears(1);
//        } else if (split.length == 2) {
//          date = LocalDate.of(
//              Integer.parseInt(split[0]),
//              Integer.parseInt(split[1]),
//              1
//          );
//          date2 = date.plusMonths(1);
//        } else {
//          date = LocalDate.of(
//              Integer.parseInt(split[0]),
//              Integer.parseInt(split[1]),
//              Integer.parseInt(split[2])
//          );
//          date2 = date.plusDays(1);
//        }
//      } catch (Exception e) {
//        throw new ParamException("非法时间格式 " + req.getDate());
//      }
//      queryWrapper
//          .between(Order::getCtime,
//              date.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli(),
//              date2.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli() - 1
//          );
//    }
//
//
//    Map<Long, HttpApi> apiMap = null;
//    if (StringUtils.isNotBlank(req.getApiName())) {
//      apiMap = apiService.list(new LambdaQueryWrapper<HttpApi>()
//          .select(HttpApi::getId, HttpApi::getName)
//          .like(HttpApi::getName, req.getApiName())
//      ).stream()
//           .collect(Collectors.toMap(HttpApi::getId, Function.identity()));
//      if(!apiMap.isEmpty()){
//        queryWrapper.in(Order::getApiId, apiMap.keySet());
//      }
//    }
//
//    final Map<Long, HttpApi> apiMap0 = apiMap;
//    List<Order> result = orderService.list(queryWrapper);
//    if(StringUtils.isBlank(req.getSearchType())){
//      result.stream()
//          .map(e->{
//            OrderTransactionResp resp = new OrderTransactionResp();
//            resp.setDate(OffsetDateTime.ofInstant(Instant.ofEpochMilli(e.getCtime()), ZoneOffset.ofHours(8))
//                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            resp.setApiId(e.getApiId());
//            if(apiMap0 != null){
//              resp.setApiName(apiMap0.get(e.getApiId()).getName());
//            }
//          })
//    }
//
//
//  }


  @GetMapping("/api-usage/time-range")
  public Resp<List<Long>> apiUsageTimeRange() {
    ApiStatus max = apiStatusService.getOne(new LambdaQueryWrapper<ApiStatus>()
        .orderByDesc(ApiStatus::getCreateDate)
        .last("limit 1")
    );
    ApiStatus min = apiStatusService.getOne(new LambdaQueryWrapper<ApiStatus>()
        .orderByAsc(ApiStatus::getCreateDate)
        .last("limit 1")
    );
    if (max == null) {
      return Resp.ok(null);
    }
    String[] maxUnits = max.getCreateDate().split("-");
    String[] minUnits = min.getCreateDate().split("-");
    LocalDate maxDate = LocalDate.of(Integer.parseInt(maxUnits[0]), Integer.parseInt(maxUnits[1]),
        Integer.parseInt(maxUnits[2]));
    LocalDate minDate = LocalDate.of(Integer.parseInt(minUnits[0]), Integer.parseInt(minUnits[1]),
        Integer.parseInt(minUnits[2]));
    List<Long> list = new ArrayList<>(2);
    list.add(minDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
    list.add(maxDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
    return Resp.ok(list);
  }


  @Operation(description = "没有year、month表示每年，有year就是指定year的每个月，month就是每天")
  @GetMapping("/api-usage")
  public Resp<ApiStatusVo> apiUsage(
      ApiUsageReq req
  ) {
    LambdaQueryWrapper<ApiStatus> queryWrapper = new LambdaQueryWrapper<>();
    if (StringUtils.isNotBlank(req.getApiName())) {
      HttpApi one = apiService.getOne(
          new LambdaQueryWrapper<HttpApi>().eq(HttpApi::getName, req.getApiName().trim()));
      if(one == null){
        return Resp.ok(null);
      }
      queryWrapper.eq(ApiStatus::getApiId, one.getId());
    }
    if (StringUtils.isNotBlank(req.getUsername())) {
      User one = userService.getOne(
          new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername().trim()));
      if(one == null){
        return Resp.ok(null);
      }
      queryWrapper.eq(ApiStatus::getUserId, one.getId());
    }

    if (ObjectUtils.allNotNull(req.getYear(), req.getMonth())) {
      //指定月的每日
      validateDate(req.getYear(), req.getMonth(), null);
      queryWrapper.likeRight(ApiStatus::getCreateDate,
          getDate(req.getYear(), req.getMonth(), null));
    } else if (req.getYear() != null) {
      //指定年的每月
      validateDate(req.getYear(), null, null);
      queryWrapper.likeRight(ApiStatus::getCreateDate, getDate(req.getYear(), null, null));
    }
    //否则就是每年

    List<ApiStatus> result = apiStatusService.list(queryWrapper);
    if (result.isEmpty()) {
      return Resp.ok(null);
    }
    List<ApiStatus> apiStatusList = null;
    if (req.getYear() != null && req.getMonth() != null) {
      apiStatusList = result;
    } else {
      HashMap<String, ApiStatus> unitMap = new HashMap<>();
      for (ApiStatus one : result) {
        String[] timeUnits = one.getCreateDate().split("-");
        String key = null;
        if (req.getYear() != null) {
          key = req.getYear() + "-" + timeUnits[1];
        } else {
          key = timeUnits[0];
        }
        ApiStatus unitApiStatus = unitMap.computeIfAbsent(key, ApiStatus::of);
        unitApiStatus.setCallTimes(unitApiStatus.getCallTimes() + one.getCallTimes());
        unitApiStatus.setSuccessTimes(unitApiStatus.getSuccessTimes() + one.getSuccessTimes());
        unitApiStatus.setTotalDuration(unitApiStatus.getTotalDuration() + one.getTotalDuration());
      }
      apiStatusList = new ArrayList<>(unitMap.values());
    }

    ApiStatusVo vo = new ApiStatusVo();
    vo.setStatus(apiStatusList.stream()
        .sorted((Comparator.comparing(ApiStatus::getCreateDate))).collect(Collectors.toList()));
    vo.setCallTimes(apiStatusList.stream().mapToInt(ApiStatus::getCallTimes).sum());
    vo.setSuccessTimes(apiStatusList.stream().mapToInt(ApiStatus::getSuccessTimes).sum());
    vo.setTotalDuration(apiStatusList.stream().mapToLong(ApiStatus::getTotalDuration).sum());
    return Resp.ok(vo);
  }

  private String getDate(Integer year, Integer month, Integer day) {
    String dateStr = "";
    if (year != null) {
      dateStr += year;
    }
    if (month != null) {
      dateStr += "-" + (month > 9 ? month : "0" + month);
    }
    if (day != null) {
      dateStr += "-" + (day > 9 ? day : "0" + day);
    }
    return dateStr;
  }

  private void validateDate(Integer year, Integer month, Integer day) {
    if (year != null && (year > LocalDate.now().getYear() || year < 2000)) {
      throw new ParamException("非法年份");
    }
    if (month != null && (month > 12 || month < 1)) {
      throw new ParamException("非法月份");
    }
    if (day != null && (day > 31 || day < 1)) {
      throw new ParamException("非法日期");
    }
  }

  @GetMapping("/api-order")
  public Resp<ApiOrderStatisticsVo> apiOrder(ApiOrderStatisticsReq req) {
    LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Order::getStatus, OrderStatusEnum.PAID.getValue());
    if (req.getApiId() != null) {
      queryWrapper.eq(Order::getApiId, req.getApiId());
    }
    if (req.getUserId() != null) {
      queryWrapper.eq(Order::getUserId, req.getUserId());
    }

    LocalDate date = null;
    LocalDate date2 = null;
    if (ObjectUtils.allNotNull(req.getYear(), req.getMonth())) {
      //指定月的每日
      validateDate(req.getYear(), req.getMonth(), null);
      if (req.getMonth() == 12) {
        date = LocalDate.of(req.getYear(), req.getMonth(), 1);
        date2 = LocalDate.of(req.getYear() + 1, 1, 1);
      } else {
        date = LocalDate.of(req.getYear(), req.getMonth(), 1);
        date2 = LocalDate.of(req.getYear(), req.getMonth() + 1, 1);
      }
    } else if (req.getYear() != null) {
      //指定年的每月
      validateDate(req.getYear(), null, null);
      date = LocalDate.of(req.getYear(), 1, 1);
      date2 = LocalDate.of(req.getYear() + 1, 1, 1);
    }
    if (date != null) {
      queryWrapper.between(Order::getCtime,
          date.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli(),
          date2.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli() - 1
      );
    }
    //否则就是每年
    return Resp.ok(getApiOrderResult(queryWrapper, req.getYear(), req.getMonth()));
  }

  private ApiOrderStatisticsVo getApiOrderResult(
      LambdaQueryWrapper<Order> queryWrapper, Integer year, Integer month
  ) {
    List<Order> result = orderService.list(queryWrapper);
    if (result.isEmpty()) {
      return null;
    }
    HashMap<String, ApiOrderStatisticsResp> unitMap = new HashMap<>();
    for (Order order : result) {
      Instant instant = Instant.ofEpochMilli(order.getCtime());
      OffsetDateTime datetime = instant.atOffset(ZoneOffset.ofHours(8));
      String key = null;
      if (year != null && month != null) {
        key = getDate(year, month, datetime.getDayOfMonth());
      } else if (year != null) {
        key = getDate(year, datetime.getMonthValue(), null);
      } else {
        key = getDate(datetime.getYear(), null, null);
      }
      ApiOrderStatisticsResp unit = unitMap.computeIfAbsent(key, ApiOrderStatisticsResp::of);
      unit.setAmount(unit.getAmount() + 1);
      unit.setTotal(unit.getTotal() + order.getActualPayment());
    }
    List<ApiOrderStatisticsResp> status = unitMap.values().stream()
        .sorted(Comparator.comparing(ApiOrderStatisticsResp::getDate)).collect(Collectors.toList());
    ApiOrderStatisticsVo vo = new ApiOrderStatisticsVo();
    vo.setStatus(status);
    vo.setTotal(status.stream().mapToDouble(ApiOrderStatisticsResp::getTotal).sum());
    vo.setAmount(status.stream().mapToInt(ApiOrderStatisticsResp::getAmount).sum());
    return vo;
  }
}

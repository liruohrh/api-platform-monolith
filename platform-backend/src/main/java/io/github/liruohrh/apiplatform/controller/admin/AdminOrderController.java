package io.github.liruohrh.apiplatform.controller.admin;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.entity.OrderStatus;
import io.github.liruohrh.apiplatform.model.req.order.ListOrderStatusReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.OrderStatusVo;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.OrderStatusService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

  @Resource
  private HttpApiService apiService;
  @Resource
  private OrderStatusService orderStatusService;
  @Resource
  private OrderService orderService;

  @GetMapping("/status")
  public Resp<PageResp<OrderStatusVo>> listOrderStatus(ListOrderStatusReq req) {
    LambdaQueryWrapper<OrderStatus> queryWrapper = new LambdaQueryWrapper<>();
    if (CollectionUtil.isNotEmpty(req.getDateRange()) && req.getDateRange().size() == 2) {
      queryWrapper
          .between(OrderStatus::getDate, new Date(req.getDateRange().get(0)),
              new Date(req.getDateRange().get(1))
          );
      queryWrapper.orderBy(req.getAmountS() != null, Boolean.TRUE.equals(req.getAmountS()), OrderStatus::getAmount);
      queryWrapper.orderBy(req.getTotalS() != null, Boolean.TRUE.equals(req.getTotalS()), OrderStatus::getTotal);
      Page<OrderStatus> page = orderStatusService.page(
          new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_ORDER_STATUS),
          queryWrapper
      );
      if (page.getRecords().isEmpty()) {
        return PageResp.empty(page);
      }
      Map<Long, HttpApi> apiMap = apiService.listByIds(
              page.getRecords().stream().map(OrderStatus::getApiId).collect(Collectors.toList())
          )
          .stream().collect(Collectors.toMap(HttpApi::getId, Function.identity()));

      return PageResp.of(
          page.getRecords().stream().map(e -> {
            OrderStatusVo vo = new OrderStatusVo();
            BeanUtils.copyProperties(e, vo);
            vo.setDate(e.getDate().toString());
            HttpApi api = apiMap.get(e.getApiId());
            if (api == null) {
              vo.setApiName("apiId=" + vo.getApiId() + "已被删除");
            } else {
              vo.setApiName(api.getName());
            }
            return vo;
          }).collect(Collectors.toList()),
          page
      );
    }

    List<OrderStatus> list = orderStatusService.list();
    if (list.isEmpty()) {
      return PageResp.empty();
    }
    Map<Long, HttpApi> apiMap = apiService.listByIds(
            list.stream().map(OrderStatus::getApiId).collect(Collectors.toList())
        )
        .stream().collect(Collectors.toMap(HttpApi::getId, Function.identity()));

    Collection<OrderStatusVo> result;
    if (Boolean.TRUE.equals(req.getIsMonth())) {
      result = list.stream()
          .collect(Collectors.groupingBy(
                  OrderStatus::getApiId,
                  Collectors.groupingBy(
                      e -> e.getDate().getYear() + "-" + e.getDate().getMonthValue(),
                      Collectors.collectingAndThen(
                          Collectors.toList(), // 先收集到列表中
                          e -> {
                            OrderStatus firstEl = e.get(0);
                            OrderStatusVo vo = new OrderStatusVo();
                            vo.setDate(
                                firstEl.getDate().getYear() + "-" + firstEl.getDate().getMonthValue());
                            vo.setApiId(firstEl.getApiId());
                            HttpApi api = apiMap.get(vo.getApiId());
                            if (api == null) {
                              vo.setApiName("apiId=" + vo.getApiId() + "已被删除");
                            } else {
                              vo.setApiName(api.getName());
                            }
                            vo.setAmount(
                                e.stream().mapToDouble(OrderStatus::getAmount).sum());
                            vo.setTotal(
                                e.stream().mapToInt(OrderStatus::getTotal).sum());
                            return vo;
                          }
                      )
                  )
              )
          ).values().stream().flatMap(e -> e.values().stream()).collect(Collectors.toList());
    } else {
      //按api总和
      result = list.stream()
          .collect(Collectors.groupingBy(
                  OrderStatus::getApiId,
                  Collectors.collectingAndThen(
                      Collectors.toList(), // 先收集到列表中
                      e -> {
                        OrderStatusVo vo = new OrderStatusVo();
                        vo.setApiId(e.get(0).getApiId());
                        HttpApi api = apiMap.get(vo.getApiId());
                        if (api == null) {
                          vo.setApiName("apiId=" + vo.getApiId() + "已被删除");
                        } else {
                          vo.setApiName(api.getName());
                        }
                        vo.setAmount(
                            e.stream().mapToDouble(OrderStatus::getAmount).sum());
                        vo.setTotal(
                            e.stream().mapToInt(OrderStatus::getTotal).sum());
                        return vo;
                      }
                  )
              )
          ).values();
    }
    return Resp.ok(new PageResp<>(
        new ArrayList<>(result),
        result.size(),
        1,
        1,
        result.size()
    ));
  }

//  public Resp<OrderStatusVo> listOrderTransactions(ListOrderStatusReq req) {
//    List<Order> list = orderService.list(
//        new LambdaQueryWrapper<Order>()
//            .eq(Order::getStatus, OrderStatusEnum.PAID.getValue())
//            .ne(Order::getPrice, 0.0)
//    );
//    if (list.isEmpty()) {
//      return Resp.ok(null);
//    }
//    Map<Long, HttpApi> apiMap = apiService.listByIds(
//            list.stream().map(Order::getApiId).collect(Collectors.toList()))
//        .stream().collect(Collectors.toMap(HttpApi::getId, Function.identity()));
//
//    //要按月
//    if (Boolean.TRUE.equals(req.getIsMonth())) {
//      Map<Long, Map<String, OrderStatusResp>> result = new HashMap<>();
//      for (Order order : list) {
//        LocalDate localDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(order.getCtime()),
//            ZoneOffset.ofHours(8)).toLocalDate();
//        OrderStatusResp e = result.computeIfAbsent(order.getApiId(), k -> new HashMap<>())
//            .computeIfAbsent(
//                localDate.getYear() + "-" + localDate.getMonthValue(), k -> {
//                  OrderStatusResp v = new OrderStatusResp();
//                  v.setDate(k);
//                  v.setApiId(order.getApiId());
//                  HttpApi api = apiMap.get(order.getApiId());
//                  v.setApiName(
//                      api == null ? "apiId=" + order.getApiId() + "已被删除" : api.getName());
//                  v.setAmount(0.0);
//                  v.setTotal(0);
//                  return v;
//                });
//        e.setAmount(e.getAmount() + order.getActualPayment());
//        e.setTotal(e.getTotal() + 1);
//      }
//      OrderStatusVo vo = new OrderStatusVo();
//      vo.setStatus(
//          result.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList()));
//      vo.setAmount(vo.getStatus().stream().mapToDouble(OrderStatusResp::getAmount).sum());
//      vo.setTotal(vo.getStatus().stream().mapToInt(OrderStatusResp::getTotal).sum());
//      return Resp.ok(vo);
//    } else {
//      //按api总和
//      Map<Long, OrderStatusResp> result = new HashMap<>();
//      for (Order order : list) {
//        OrderStatusResp e = result
//            .computeIfAbsent(
//                order.getApiId(), k -> {
//                  OrderStatusResp v = new OrderStatusResp();
//                  v.setApiId(k);
//                  HttpApi api = apiMap.get(k);
//                  v.setApiName(api == null ? "apiId=" + k + "已被删除" : api.getName());
//                  v.setTotal(0);
//                  v.setAmount(0.0);
//                  return v;
//                });
//        e.setAmount(e.getAmount() + order.getActualPayment());
//        e.setTotal(e.getTotal() + 1);
//      }
//      OrderStatusVo vo = new OrderStatusVo();
//      vo.setStatus(new ArrayList<>(result.values()));
//      vo.setTotal(vo.getStatus().stream().mapToInt(OrderStatusResp::getTotal).sum());
//      vo.setAmount(vo.getStatus().stream().mapToDouble(OrderStatusResp::getAmount).sum());
//      return Resp.ok(vo);
//    }
//  }

  @GetMapping("/api-order/time-range")
  public Resp<List<Long>> apiOrderTimeRange() {
    Order max = orderService.getOne(new LambdaQueryWrapper<Order>()
        .orderByDesc(Order::getCtime)
        .last("limit 1")
    );
    Order min = orderService.getOne(new LambdaQueryWrapper<Order>()
        .orderByAsc(Order::getCtime)
        .last("limit 1")
    );
    if (max == null) {
      return Resp.ok(null);
    }
    List<Long> list = new ArrayList<>(2);
    list.add(min.getCtime());
    list.add(max.getCtime());
    return Resp.ok(list);
  }
}

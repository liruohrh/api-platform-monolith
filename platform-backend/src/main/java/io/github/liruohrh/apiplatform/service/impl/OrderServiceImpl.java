package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.mapper.OrderMapper;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSearchReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSortReq;
import io.github.liruohrh.apiplatform.model.vo.OrderVo;
import io.github.liruohrh.apiplatform.service.OrderService;
import javax.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author LYM
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-08-12 17:39:30
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService {
  @Resource
  private OrderMapper orderMapper;
  @Override
  public Page<OrderVo> listOrder(
      PageReq<OrderSearchReq, OrderSortReq> pageReq,
      Long userId
  ) {
    if (pageReq.getCurrent() == null) {
      pageReq.setCurrent(1);
    }
    OrderSearchReq search = pageReq.getSearch();
    OrderSortReq sort = pageReq.getSort();
    QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
    if (search != null) {
      if (StringUtils.isNotBlank(search.getOrderId())) {
        queryWrapper.eq("goods_order.order_id", search.getOrderId());
      }
      if (search.getPrice() != null) {
        queryWrapper.eq("goods_order.price", search.getPrice());
      }
      if (search.getCtime() != null) {
        search.getCtime().query(queryWrapper, "goods_order.ctime");
      }
      if (search.getUtime() != null) {
        search.getUtime().query(queryWrapper, "goods_order.utime");
      }
      if (search.getStatus() != null) {
        queryWrapper.eq("goods_order.status", search.getStatus());
      }
    }
    if (sort != null) {
      queryWrapper.orderBy(sort.getCtimeS() != null, Boolean.TRUE.equals(sort.getCtimeS()),
          "goods_order.ctime");
      queryWrapper.orderBy(sort.getUtimeS() != null, Boolean.TRUE.equals(sort.getUtimeS()),
          "goods_order.utime");
    }
    if(sort == null || ObjectUtils.allNull(sort.getActualPayment(), sort.getAmount(), sort.getUtimeS(), sort.getCtimeS())){
      queryWrapper.orderByDesc("goods_order.ctime");
    }
    LambdaQueryWrapper<Order> lambdaQueryWrapper = queryWrapper.lambda();
    if (search != null) {
      if (search.getActualPayment() != null) {
        search.getActualPayment().query(lambdaQueryWrapper, Order::getActualPayment);
      }
      if (search.getApiId() != null) {
        lambdaQueryWrapper.eq(Order::getApiId, search.getApiId());
      }
    }
    if (sort != null) {
      lambdaQueryWrapper.orderBy(sort.getActualPayment() != null, Boolean.TRUE.equals(sort.getActualPayment() ),
          Order::getActualPayment);
      lambdaQueryWrapper.orderBy(sort.getAmount() != null, Boolean.TRUE.equals(sort.getAmount()),
          Order::getAmount);
    }
    return orderMapper.pageOrder(
        new Page<>(pageReq.getCurrent(), CommonConstant.PAGE_MAX_SIZE_ORDER),
        lambdaQueryWrapper,
        userId,
        search == null ? null : (StrUtil.isEmpty(search.getApiName()) ? null : search.getApiName())
    );
  }
}





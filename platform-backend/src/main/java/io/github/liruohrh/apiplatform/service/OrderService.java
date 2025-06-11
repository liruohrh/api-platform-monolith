package io.github.liruohrh.apiplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSearchReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSortReq;
import io.github.liruohrh.apiplatform.model.vo.OrderVo;

/**
* @author LYM
* @description 针对表【order】的数据库操作Service
* @createDate 2024-08-12 17:39:30
*/
public interface OrderService extends IService<Order> {

  Page<OrderVo> listOrder(PageReq<OrderSearchReq, OrderSortReq> pageReq,  Long userId);


}

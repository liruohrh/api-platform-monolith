package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.model.entity.OrderStatus;
import io.github.liruohrh.apiplatform.service.OrderStatusService;
import io.github.liruohrh.apiplatform.mapper.OrderStatusMapper;
import org.springframework.stereotype.Service;

/**
* @author liruohrh
* @description 针对表【order_status】的数据库操作Service实现
* @createDate 2025-03-08 13:41:02
*/
@Service
public class OrderStatusServiceImpl extends ServiceImpl<OrderStatusMapper, OrderStatus>
    implements OrderStatusService{

}





package io.github.liruohrh.apiplatform.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.vo.OrderVo;
import org.apache.ibatis.annotations.Param;

/**
* @author LYM
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-08-12 17:39:30
* @Entity io.github.liruohrh.apiplatform.model.entity.Order
*/
public interface OrderMapper extends BaseMapper<Order> {
  Page<OrderVo> pageOrder(
      Page<OrderVo> page,
      @Param(Constants.WRAPPER) Wrapper<Order> qw,
      @Param("userId") Long userId,
      @Param("apiName") String apiName
  );
}





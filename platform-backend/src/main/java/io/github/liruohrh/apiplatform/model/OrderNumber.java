package io.github.liruohrh.apiplatform.model;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.liruohrh.apiplatform.model.entity.Order;
import java.io.Serializable;
import lombok.Data;

@Data
public class OrderNumber implements Serializable {
  private Number value;
  private Number min;
  private Boolean minEq;
  private Number max;
  private Boolean maxEq;
  private static final long serialVersionUID = 1L;
  public void query(LambdaQueryWrapper<Order> queryWrapper, SFunction<Order, ?> getter){
    if(min == null && max == null && value != null){

      queryWrapper.eq(getter, value);
      return;
    }
    //2
    //[2,), [2,3)
    if(minEq !=null && min != null){
      if(minEq){
        queryWrapper.ge(
            getter,
            min
        );
      }else{
        queryWrapper.gt(
            getter,
            min
        );
      }
    }
    //[,2), [2,3)
    if(maxEq !=null && max != null){
      if(maxEq){
        queryWrapper.le(
            getter,
            max
        );
      }else{
        queryWrapper.lt(
            getter,
            max
        );
      }
    }
  }
  public void query(QueryWrapper<Order> queryWrapper, String getter){
    if(min == null && max == null && value != null){
      queryWrapper.eq(getter, value);
      return;
    }
    //2
    //[2,), [2,3)
    if(minEq !=null && min != null){
      if(minEq){
        queryWrapper.ge(
            getter,
            min
        );
      }else{
        queryWrapper.gt(
            getter,
            min
        );
      }
    }
    //[,2), [2,3)
    if(maxEq !=null && max != null){
      if(maxEq){
        queryWrapper.le(
            getter ,
            max
        );
      }else{
        queryWrapper.lt(
            getter,
            max
        );
      }
    }
  }
}

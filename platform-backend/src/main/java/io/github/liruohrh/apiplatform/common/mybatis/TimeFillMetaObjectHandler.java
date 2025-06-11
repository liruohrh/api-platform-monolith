package io.github.liruohrh.apiplatform.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;


public class TimeFillMetaObjectHandler implements MetaObjectHandler {
  private final String createTimeFieldName;
  private final String updateTimeFieldName;

  public TimeFillMetaObjectHandler(String createTimeFieldName, String updateTimeFieldName) {
    this.createTimeFieldName = createTimeFieldName;
    this.updateTimeFieldName = updateTimeFieldName;
  }


  @Override
  public void insertFill(MetaObject metaObject) {
    if( metaObject.hasGetter(createTimeFieldName)){
      metaObject.setValue(createTimeFieldName, System.currentTimeMillis());
    }
   if( metaObject.hasGetter(updateTimeFieldName)){
     metaObject.setValue(updateTimeFieldName, System.currentTimeMillis());
   }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    metaObject.setValue(updateTimeFieldName, System.currentTimeMillis());
  }
}

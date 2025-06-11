package io.github.liruohrh.apiplatform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.liruohrh.apiplatform.common.mybatis.TimeFillMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("io.github.liruohrh.apiplatform.mapper")
public class MybatisConfig {
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
    paginationInnerInterceptor.setOptimizeJoin(false);
    interceptor.addInnerInterceptor(paginationInnerInterceptor);

    return interceptor;
  }
  @Bean
  public TimeFillMetaObjectHandler timeFillMetaObjectHandler(){
    return new TimeFillMetaObjectHandler("ctime", "utime");
  }
}

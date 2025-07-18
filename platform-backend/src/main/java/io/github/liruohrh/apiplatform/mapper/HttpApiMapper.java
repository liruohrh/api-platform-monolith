package io.github.liruohrh.apiplatform.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.vo.ApiAndUsageVo;
import org.apache.ibatis.annotations.Param;

/**
* @author LYM
* @description 针对表【http_api】的数据库操作Mapper
* @createDate 2024-08-12 17:39:30
* @Entity io.github.liruohrh.apiplatform.model.entity.HttpApi
*/
public interface HttpApiMapper extends BaseMapper<HttpApi> {
  Page<ApiAndUsageVo> listApiUsage(
      Page<ApiAndUsageVo> page,
      @Param(Constants.WRAPPER) Wrapper<HttpApi> qw,
      @Param("userId") Long userId
  );
}





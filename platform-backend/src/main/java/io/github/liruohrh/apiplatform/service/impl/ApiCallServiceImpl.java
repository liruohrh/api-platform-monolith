package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.mapper.ApiCallMapper;
import io.github.liruohrh.apiplatform.model.entity.ApiCall;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import org.springframework.stereotype.Service;

/**
* @author LYM
* @description 针对表【api_call】的数据库操作Service实现
* @createDate 2024-08-12 17:39:30
*/
@Service
public class ApiCallServiceImpl extends ServiceImpl<ApiCallMapper, ApiCall>
    implements ApiCallService {
}





package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.service.ApiCallLogService;
import io.github.liruohrh.apiplatform.model.entity.ApiCallLog;
import io.github.liruohrh.apiplatform.mapper.ApiCallLogMapper;
import org.springframework.stereotype.Service;

/**
* @author LYM
* @description 针对表【api_call_log】的数据库操作Service实现
* @createDate 2024-08-12 17:39:30
*/
@Service
public class ApiCallLogServiceImpl extends ServiceImpl<ApiCallLogMapper, ApiCallLog>
    implements ApiCallLogService {

}





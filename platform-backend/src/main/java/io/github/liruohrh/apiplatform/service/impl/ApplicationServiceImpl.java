package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.model.entity.Application;
import io.github.liruohrh.apiplatform.service.ApplicationService;
import io.github.liruohrh.apiplatform.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;

/**
* @author LYM
* @description 针对表【application】的数据库操作Service实现
* @createDate 2024-08-12 17:39:30
*/
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application>
    implements ApplicationService {

}





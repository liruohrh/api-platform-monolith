package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.service.CommentService;
import io.github.liruohrh.apiplatform.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author LYM
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2024-08-12 17:39:30
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService {

}





package io.github.liruohrh.apiplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import java.util.Collection;
import java.util.List;

/**
* @author LYM
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2024-08-12 17:39:30
* @Entity io.github.liruohrh.apiplatform.model.entity.Comment
*/
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> listByIds(Collection<Long> ids);
    double calculateScore(Long apiId);
    int recover(Long id);
}





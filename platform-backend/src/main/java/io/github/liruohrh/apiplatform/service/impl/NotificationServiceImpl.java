package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.model.entity.Notification;
import io.github.liruohrh.apiplatform.model.enume.NotificationType;
import io.github.liruohrh.apiplatform.service.NotificationService;
import io.github.liruohrh.apiplatform.mapper.NotificationMapper;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author LYM
 * @description 针对表【notification】的数据库操作Service实现
 * @createDate 2024-12-22 21:56:08
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
    implements NotificationService {

  @Override
  public Set<String> userUnreadReplies(Collection<Long> replyIds) {
    return list(
        new LambdaQueryWrapper<Notification>()
            .eq(Notification::getNoticeType, NotificationType.COMMENT.getValue())
            .eq(Notification::getTitle, NotificationType.Comment.REPLY.getValue())
            .eq(Notification::getUserId, LoginUserHolder.getUserId())
            .eq(Notification::getIsRead, false)
            .in(Notification::getContent,
                replyIds.stream().map(Object::toString).collect(Collectors.toSet()))
    )
        .stream()
        .map(Notification::getContent)
        .collect(Collectors.toSet());
  }
}





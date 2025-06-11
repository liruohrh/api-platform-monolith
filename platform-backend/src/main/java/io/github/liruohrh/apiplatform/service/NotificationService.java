package io.github.liruohrh.apiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.liruohrh.apiplatform.model.entity.Notification;
import java.util.Collection;
import java.util.Set;

/**
* @author LYM
* @description 针对表【notification】的数据库操作Service
* @createDate 2024-12-22 21:56:08
*/
public interface NotificationService extends IService<Notification> {
  Set<String> userUnreadReplies(Collection<Long> replyIds);
}

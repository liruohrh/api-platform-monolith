package io.github.liruohrh.apiplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.mapper.CommentMapper;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.model.entity.CommentInteraction;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Notification;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.NotificationType;
import io.github.liruohrh.apiplatform.model.req.CommonPageReq;
import io.github.liruohrh.apiplatform.model.req.comment.CommentReplyReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.CommentReplyVo;
import io.github.liruohrh.apiplatform.model.vo.CommentVo;
import io.github.liruohrh.apiplatform.model.vo.UserCommentVo;
import io.github.liruohrh.apiplatform.service.CommentInteractionService;
import io.github.liruohrh.apiplatform.service.CommentService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.NotificationService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/comment/user")
@RestController
public class UserCommentController {

  @Resource
  private CommentMapper commentMapper;
  private final HttpApiService apiService;
  private final CommentInteractionService commentInteractionService;
  private final CommentService commentService;
  private final UserService userService;
  private final NotificationService notificationService;

  public UserCommentController(HttpApiService apiService,
      CommentInteractionService commentInteractionService, CommentService commentService,
      UserService userService,
      NotificationService notificationService) {
    this.apiService = apiService;
    this.commentInteractionService = commentInteractionService;
    this.commentService = commentService;
    this.userService = userService;
    this.notificationService = notificationService;
  }

  @GetMapping("/reply/read")
  public Resp<Void> readReply(@RequestParam(value = "commentId", required = false) Long commentId) {
    LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<Notification>()
        .eq(Notification::getNoticeType, NotificationType.COMMENT.getValue())
        .eq(Notification::getUserId, LoginUserHolder.getUserId())
        .eq(Notification::getTitle, NotificationType.Comment.REPLY.getValue())
        .eq(Notification::getIsRead, false)
        .eq(commentId != null, Notification::getContent,
            commentId != null ? commentId.toString() : "")
        .set(Notification::getIsRead, true);
    notificationService.update(updateWrapper);
    return Resp.ok(null);
  }

  @GetMapping("/reply/count")
  public Resp<Long> getUnReadReplyCount(
  ) {
    return Resp.ok(notificationService.count(new LambdaQueryWrapper<Notification>()
        .eq(Notification::getNoticeType, NotificationType.COMMENT.getValue())
        .eq(Notification::getUserId, LoginUserHolder.getUserId())
        .eq(Notification::getTitle, NotificationType.Comment.REPLY.getValue())
        .eq(Notification::getIsRead, false)
    ));
  }

  /**
   * 回复用户的.
   * 注意：如果被删除了，肯定就找不到了，但是他可能没被删除，但是用户可能删除了自己的评论
   */
  @GetMapping("/reply")
  public Resp<PageResp<CommentReplyVo>> listReplyToUserComment(
      CommonPageReq commonPageReq
  ) {
    Page<Comment> page = commentService.page(
        new Page<>(commonPageReq.getCurrent(), CommonConstant.PAGE_MAX_SIZE_COMMENT),
        new LambdaQueryWrapper<Comment>()
            .eq(Comment::getReplyToUserId, LoginUserHolder.getUserId())
            .orderByDesc(Comment::getCtime)
    );
    if (page.getRecords().isEmpty()) {
      return PageResp.empty(page);
    }
    List<Long> replyIds = page.getRecords().stream()
        .map(Comment::getId)
        .collect(Collectors.toList());
    Set<String> unreadReplyIdSet = notificationService.userUnreadReplies(replyIds);

    Map<Long, HttpApi> apiMap = apiService.listByIds(page.getRecords().stream()
            .map(Comment::getApiId)
            .collect(Collectors.toSet()))
        .stream().collect(Collectors.toMap(HttpApi::getId, Function.identity()));
    Map<Long, User> replierMap = userService.listByIds(page.getRecords().stream()
            .map(Comment::getUserId)
            .collect(Collectors.toSet()))
        .stream().collect(Collectors.toMap(User::getId, Function.identity()));
    List<Comment> userCommentList = commentService.listByIds(page.getRecords().stream()
        .map(Comment::getReplyToCommentId)
        .collect(Collectors.toSet())
    );
    Map<Long, Comment> userCommentMap = userCommentList.isEmpty() ?
        Collections.emptyMap()
        : userCommentList.stream()
            .collect(Collectors.toMap(Comment::getId, Function.identity()));

    Map<Long, CommentInteraction> replyInteractionMap = commentInteractionService.list(
        new LambdaQueryWrapper<CommentInteraction>()
            .eq(CommentInteraction::getUserId, LoginUserHolder.getUserId())
            .in(CommentInteraction::getCommentId, replyIds)
    ).stream().collect(Collectors.toMap(CommentInteraction::getCommentId, Function.identity()));

    return Resp.ok(new PageResp<>(
        page.getRecords().stream()
            .map(e -> {
              CommentReplyVo vo = new CommentReplyVo();
              BeanUtils.copyProperties(e, vo);
              vo.setIsRead(!unreadReplyIdSet.contains(e.getId().toString()));
              vo.setApiName(apiMap.get(e.getApiId()).getName());
              vo.setUserNickname(replierMap.get(e.getUserId()).getNickname());

              Comment userComment = userCommentMap.get(e.getReplyToCommentId());
              if(userComment != null){
                CommentVo userCommentVo = new CommentVo();
                BeanUtils.copyProperties(userComment, userCommentVo);
                vo.setUserComment(userCommentVo);
              }

              CommentInteraction replyInteraction = replyInteractionMap.get(e.getId());
              vo.setIsFavor(replyInteraction != null && replyInteraction.getFavor());
              return vo;
            }).collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize())
    );
  }

  /**
   * 包括用户的回复。
   * 注意：回复对象的评论可能被删除
   *
   * @param commonPageReq
   * @return
   */
  @GetMapping
  public Resp<PageResp<UserCommentVo>> listUserComment(
      CommonPageReq commonPageReq
  ) {
    Page<Comment> page = commentService.page(
        new Page<>(commonPageReq.getCurrent(), CommonConstant.PAGE_MAX_SIZE_COMMENT),
        new LambdaQueryWrapper<Comment>()
            .eq(Comment::getUserId, LoginUserHolder.getUserId())
            .orderByDesc(Comment::getCtime)
    );
    if (page.getRecords().isEmpty()) {
      return PageResp.empty(page);
    }

    Map<Long, HttpApi> apiMap = apiService.listByIds(page.getRecords().stream()
            .map(Comment::getApiId)
            .collect(Collectors.toSet()))
        .stream().collect(Collectors.toMap(HttpApi::getId, Function.identity()));

    //回复的对象
    Set<Long> addresseeIdSet = page.getRecords().stream()
        .map(Comment::getReplyToUserId)
        .filter(e -> e != CommonConstant.ROOT_COMMENT_ROOT_ID)
        .collect(Collectors.toSet());
    //可能没有回复
    Map<Long, User> addresseeMap =
        addresseeIdSet.isEmpty()
            ? Collections.emptyMap()
            : userService.listByIds(addresseeIdSet)
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));

    //回复的对象的评论
    Set<Long> addresseeCommentIdSet = page.getRecords().stream()
        .map(Comment::getReplyToCommentId)
        .filter(e -> e != CommonConstant.ROOT_COMMENT_ROOT_ID)
        .collect(Collectors.toSet());
    Map<Long, Comment> addresseeCommentMap =
        addresseeCommentIdSet.isEmpty()
            ? Collections.emptyMap()
            : commentService.listByIds(addresseeCommentIdSet)
                .stream().collect(Collectors.toMap(Comment::getId, Function.identity()));

    return Resp.ok(new PageResp<>(
        page.getRecords().stream()
            .map(e -> {
              UserCommentVo vo = new UserCommentVo();
              BeanUtils.copyProperties(e, vo);
              vo.setApiName(apiMap.get(e.getApiId()).getName());
              vo.setIsReply(e.getReplyToCommentId() != CommonConstant.ROOT_COMMENT_ROOT_ID);
              User addressee = addresseeMap.get(e.getReplyToUserId());
              if (addressee != null) {
                vo.setAddresseeNickname(addressee.getNickname());
              }
              Comment addresseeComment = addresseeCommentMap.get(e.getReplyToCommentId());
              if (addresseeComment != null) {
                CommentVo commentVo = new CommentVo();
                BeanUtils.copyProperties(addresseeComment, commentVo);
                vo.setAddresseeComment(commentVo);
              }
              return vo;
            }).collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize())
    );
  }

  @Transactional(rollbackFor = Exception.class)
  @PostMapping("/reply")
  public Resp<CommentVo> replyComment(
      @RequestBody CommentReplyReq commentReplyReq
  ) {
    Comment comment = new Comment();
    BeanUtils.copyProperties(commentReplyReq, comment);
    comment.setUserId(LoginUserHolder.getUserId());

    Comment replyToComment = commentService.getById(commentReplyReq.getReplyToCommentId());
    if (replyToComment == null) {
      throw new ParamException("要回复的评论不存在");
    }
    if (replyToComment.getRootCommentId() == CommonConstant.ROOT_COMMENT_ROOT_ID) {
      comment.setRootCommentId(replyToComment.getId());
    } else {
      comment.setRootCommentId(replyToComment.getRootCommentId());
    }
    comment.setReplyToCommentId(replyToComment.getId());
    comment.setReplyToUserId(replyToComment.getUserId());
    commentService.update(new LambdaUpdateWrapper<Comment>()
        .eq(Comment::getId, replyToComment.getId())
        .set(Comment::getReplyCount, replyToComment.getReplyCount() + 1));

    MustUtils.dbSuccess(commentService.save(comment));

    HttpApi api = apiService.getById(commentReplyReq.getApiId());
    if (api == null) {
      throw new ParamException("api不存在");
    }
//    apiService.update(new LambdaUpdateWrapper<HttpApi>()
//        .set(HttpApi::getScore, commentMapper.calculateScore(api.getId())));

    Notification notification = new Notification();
    notification.setIsRead(false);
    notification.setNoticeType(NotificationType.COMMENT.getValue());
    notification.setUserId(comment.getReplyToUserId());
    notification.setTitle(NotificationType.Comment.REPLY.getValue());
    notification.setContent(comment.getId() + "");
    MustUtils.dbSuccess(notificationService.save(notification));

    User replyToUser = userService.getById(comment.getReplyToUserId());

    CommentVo commentVo = new CommentVo();
    BeanUtils.copyProperties(commentService.getById(comment.getId()), commentVo);

    commentVo.setUsername(LoginUserHolder.get().getUsername());
    commentVo.setUserNickName(LoginUserHolder.get().getNickname());
    commentVo.setReplyToUsername(replyToUser.getUsername());
    commentVo.setReplyToUserNickname(replyToUser.getNickname());
    return Resp.ok(commentVo);
  }
}

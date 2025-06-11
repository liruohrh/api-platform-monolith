package io.github.liruohrh.apiplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.model.entity.CommentInteraction;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.req.CommonPageReq;
import io.github.liruohrh.apiplatform.model.req.comment.CommentAddReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.CommentVo;
import io.github.liruohrh.apiplatform.service.CommentInteractionService;
import io.github.liruohrh.apiplatform.service.CommentService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/comment")
@RestController
public class CommentController {

  private final CommentService commentService;
  private final OrderService orderService;
  private final CommentInteractionService commentInteractionService;
  private final UserService userService;
  private final HttpApiService apiService;

  public CommentController(CommentService commentService,
      OrderService orderService, CommentInteractionService commentInteractionService,
      UserService userService,
      HttpApiService apiService) {
    this.commentService = commentService;
    this.orderService = orderService;
    this.commentInteractionService = commentInteractionService;
    this.userService = userService;
    this.apiService = apiService;
  }

  /**
   * @param apiId         非空就是api的评论, 含5条最新回复
   * @param rootId        非空就是评论的回复
   * @param commonPageReq
   * @return
   */
  @GetMapping
  public Resp<PageResp<CommentVo>> listComment(
      @RequestParam(value = "apiId", required = false) Long apiId,
      @RequestParam(value = "rootId", required = false) Long rootId,
      @Validated CommonPageReq commonPageReq
  ) {
    if (ObjectUtils.allNull(apiId, rootId) || ObjectUtils.allNotNull(apiId, rootId)) {
      throw new ParamException("only require apiId or rootId");
    }
    Page<Comment> page = commentService.page(
        new Page<>(commonPageReq.getCurrent(), CommonConstant.PAGE_MAX_SIZE_COMMENT),
        new LambdaQueryWrapper<Comment>()
            .eq(apiId != null, Comment::getApiId, apiId)
            .eq(Comment::getRootCommentId,
                apiId == null ? rootId : CommonConstant.ROOT_COMMENT_ROOT_ID)
            .orderByDesc(Comment::getCtime)
    );
    if (page.getRecords().isEmpty()) {
      return PageResp.empty(page);
    }

    HashSet<Long> userIdSet = page.getRecords().stream()
        .map(Comment::getUserId).collect(Collectors.toCollection(HashSet::new));
    userIdSet.addAll(page.getRecords().stream()
        .map(Comment::getReplyToUserId).collect(Collectors.toList()));

    ArrayList<Long> commentIds = page.getRecords().stream().map(Comment::getId)
        .collect(Collectors.toCollection(ArrayList::new));

    Map<Long, List<Comment>> subCommentMap = null;
    if (rootId == null) {
      //todo 暂时就用最简单的方式
      @SuppressWarnings("unchecked")
      CompletableFuture<SimpleEntry<Long, List<Comment>>>[] cfs = page.getRecords().stream()
          .map(e -> CompletableFuture.supplyAsync(
              () -> new SimpleEntry<>(e.getId(),
                  commentService.list(new LambdaQueryWrapper<Comment>()
                      .eq(Comment::getRootCommentId, e.getId())
                      .orderByDesc(Comment::getCtime)
                      .last("limit 6")))
          ))
          .toArray(CompletableFuture[]::new);
      CompletableFuture.allOf(cfs).join();
      subCommentMap = Arrays.stream(cfs).map(e -> {
        try {
          return e.get();
        } catch (InterruptedException | ExecutionException ex) {
          throw new RuntimeException(ex);
        }
      }).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

      for (List<Comment> subComments : subCommentMap.values()) {
        commentIds.addAll(subComments.stream()
            .map(Comment::getId)
            .collect(Collectors.toList()));
        userIdSet.addAll(subComments.stream()
            .map(Comment::getUserId)
            .limit(5)
            .collect(Collectors.toList()));
        userIdSet.addAll(page.getRecords().stream()
            .map(Comment::getReplyToUserId)
            .limit(5)
            .collect(Collectors.toList()));
      }
    }

    userIdSet.remove(0L);
    Map<Long, User> userMap = userService.listByIds(userIdSet)
        .stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));
    Long loginUserId = LoginUserHolder.getUserId();
    Map<Long, CommentInteraction> commentInteractionMap = loginUserId == null ? Collections.emptyMap() : commentInteractionService.list(
          new LambdaQueryWrapper<CommentInteraction>()
              .eq(CommentInteraction::getUserId, loginUserId)
              .in(CommentInteraction::getCommentId, commentIds)
        ).stream()
        .collect(Collectors.toMap(CommentInteraction::getCommentId, Function.identity()));
    final Map<Long, List<Comment>> subCommentMap2 = subCommentMap;
    return Resp.ok(new PageResp<>(
        page.getRecords().stream()
            .map(e -> {
              CommentVo commentVo = new CommentVo();
              BeanUtils.copyProperties(e, commentVo);
              User commentUser = userMap.get(e.getUserId());
              commentVo.setUserNickName(commentUser.getNickname());
              commentVo.setUsername(commentUser.getUsername());
              User commentReplyToUser = userMap.get(e.getReplyToUserId());
              if(commentReplyToUser != null){
                commentVo.setReplyToUserNickname(commentReplyToUser.getNickname());
                commentVo.setReplyToUsername(commentReplyToUser.getUsername());
              }

              CommentInteraction commentInteraction = commentInteractionMap.get(e.getId());
              commentVo.setIsFavor(commentInteraction != null && commentInteraction.getFavor());

              if (subCommentMap2 != null) {
                commentVo.setSubCommentList(subCommentMap2.get(e.getId()).stream().map(e2 -> {
                  CommentVo c2 = new CommentVo();
                  BeanUtils.copyProperties(e2, c2);
                  User commentUser2 = userMap.get(e2.getUserId());
                  if (commentUser2 != null) {
                    c2.setUserNickName(commentUser2.getNickname());
                    c2.setUsername(commentUser2.getNickname());
                  }
                  User commentReplyToUser2 = userMap.get(e2.getReplyToUserId());
                  if(commentReplyToUser2 != null){
                    c2.setReplyToUserNickname(commentReplyToUser2.getNickname());
                    c2.setReplyToUsername(commentReplyToUser2.getUsername());
                  }

                  CommentInteraction commentInteraction2 = commentInteractionMap.get(e2.getId());
                  c2.setIsFavor(commentInteraction2 != null && commentInteraction2.getFavor());
                  return c2;
                }).collect(Collectors.toList()));
              }
              return commentVo;
            }).collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize())
    );
  }
  @Transactional(rollbackFor = Exception.class)
  @PostMapping("/{id}/favor")
  public Resp<Boolean> favorComment(@PathVariable("id") Long id) {
    Comment comment = commentService.getById(id);
    if (comment == null) {
      throw new ParamException("评论不存在");
    }
    CommentInteraction commentInteraction = commentInteractionService.getOne(
        new LambdaQueryWrapper<CommentInteraction>()
            .eq(CommentInteraction::getCommentId, id)
            .eq(CommentInteraction::getUserId, LoginUserHolder.getUserId()));
    if (commentInteraction == null) {
      commentInteraction = new CommentInteraction();
      commentInteraction.setCommentId(id);
      commentInteraction.setUserId(LoginUserHolder.getUserId());
      commentInteraction.setFavor(true);
    } else {
      commentInteraction.setFavor(!commentInteraction.getFavor());
    }
    commentInteractionService.saveOrUpdate(commentInteraction);
    commentService.update(new LambdaUpdateWrapper<Comment>()
        .eq(Comment::getId, id)
        .set(Comment::getFavorCount, commentInteraction.getFavor() ? comment.getFavorCount() + 1
            : comment.getFavorCount() - 1));
    return Resp.ok(true);
  }

  /**
   * 注意，删除了这条评论，不会删除其下的所有回复。
   */
  @Transactional(rollbackFor = Exception.class)
  @DeleteMapping("/{id}")
  public Resp<Boolean> deleteComment(@PathVariable("id") Long id) {
    Comment comment = commentService.getById(id);
    if (comment == null) {
      throw new ParamException("评论不存在");
    }
    Long apiId = comment.getApiId();
    MustUtils.dbSuccess(commentService.removeById(id));

    if(comment.getReplyToCommentId() != CommonConstant.ROOT_COMMENT_ROOT_ID){
      commentService.update(new LambdaUpdateWrapper<Comment>()
          .eq(Comment::getId, comment.getReplyToCommentId())
          .set(Comment::getReplyCount, commentService.getById(comment.getReplyToCommentId()).getReplyCount() - 1)
      );
    }
    commentInteractionService.remove(new LambdaQueryWrapper<CommentInteraction>()
        .eq(CommentInteraction::getCommentId, id)
    );
    apiService.updateScore(apiId);
    return Resp.ok(true);
  }

  @GetMapping("/{id}")
  public Resp<Comment> getComment(@PathVariable("id") Long id) {
    return Resp.ok(commentService.getById(id));
  }

  @Transactional(rollbackFor = Exception.class)
  @PostMapping
  public Resp<CommentVo> postComment(
      @RequestBody CommentAddReq commentAddReq
  ) {
    Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
        .eq(Order::getId, commentAddReq.getOrderId())
        .eq(Order::getUserId, LoginUserHolder.getUserId())
    );
    if (order == null) {
      throw new ParamException("无该api的订单，不能进行评论");
    }
    if(order.getIsComment()){
      throw new ParamException("该api已评论，一个订单只能评论一次");
    }
    Comment comment = new Comment();
    BeanUtils.copyProperties(commentAddReq, comment);
    HttpApi api = apiService.getById(commentAddReq.getApiId());
    if (api == null) {
      throw new ParamException("api不存在");
    }
    comment.setUserId(LoginUserHolder.getUserId());
    comment.setReplyToCommentId((long) CommonConstant.ROOT_COMMENT_ROOT_ID);
    comment.setRootCommentId((long) CommonConstant.ROOT_COMMENT_ROOT_ID);
    comment.setReplyToUserId((long) CommonConstant.ROOT_COMMENT_ROOT_ID);
    MustUtils.dbSuccess(commentService.save(comment));

    apiService.updateScore(api.getId());
    orderService.update(new LambdaUpdateWrapper<Order>()
        .eq(Order::getId, commentAddReq.getOrderId())
        .set(Order::getIsComment, true));

    CommentVo commentVo = new CommentVo();
    BeanUtils.copyProperties(commentService.getById(comment.getId()), commentVo);
    return Resp.ok(commentVo);
  }

}

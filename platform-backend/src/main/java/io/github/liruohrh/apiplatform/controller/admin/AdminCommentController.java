package io.github.liruohrh.apiplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.req.comment.CommentPageReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.CommentPageVo;
import io.github.liruohrh.apiplatform.model.vo.CommentVo;
import io.github.liruohrh.apiplatform.service.CommentService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.NotificationService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuth(mustRole = "ADMIN")
@RequestMapping("/admin/comment")
@RestController
public class AdminCommentController {
  @Resource
  private CommentService commentService;
  @Resource
  private UserService userService;
  @Resource
  private HttpApiService apiService;
  @Resource
  private NotificationService notificationService;


  @GetMapping
  public Resp<PageResp<CommentPageVo>> listAllComment(
       @ParameterObject CommentPageReq req
  ) {
    LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
    if(ObjectUtils.allNull(req.getCtime(), req.getFavorCount(), req.getReplyCount())){
      queryWrapper.orderByDesc(Comment::getCtime);
    }else{
      queryWrapper.orderBy(req.getCtime() != null, Boolean.TRUE.equals(req.getCtime()), Comment::getCtime);
      queryWrapper.orderBy(req.getFavorCount() != null, Boolean.TRUE.equals(req.getFavorCount()), Comment::getFavorCount);
      queryWrapper.orderBy(req.getReplyCount() != null, Boolean.TRUE.equals(req.getReplyCount()), Comment::getReplyCount);
    }

    if(Boolean.TRUE.equals(req.getIsRoot()) && !Boolean.TRUE.equals(req.getReplyToMe())){
      queryWrapper.eq(Comment::getRootCommentId, CommonConstant.ROOT_COMMENT_ROOT_ID);
    }if(req.getRootCommentId() != null){
      queryWrapper.eq(Comment::getRootCommentId, req.getRootCommentId());
    }else if(req.getReplyToCommentId() != null && !Boolean.TRUE.equals(req.getReplyToMe())){
      queryWrapper.eq(Comment::getReplyToCommentId, req.getReplyToCommentId());
    }

    if(StringUtils.isNotBlank(req.getUsername())){
      User one = userService.getOne(new LambdaQueryWrapper<User>()
          .eq(User::getUsername, req.getUsername()));
      if(one == null){
        return Resp.ok(null);
      }
      if(req.getIsUserReply() == null || !req.getIsUserReply()){
        queryWrapper.eq(Comment::getUserId, one.getId());
      }else{
        queryWrapper.eq(Comment::getReplyToUserId, one.getId());
      }
    }else if(Boolean.TRUE.equals(req.getReplyToMe())){
      queryWrapper.eq(Comment::getReplyToUserId, LoginUserHolder.getUserId());
    }

    if(req.getApiId() != null){
      queryWrapper.eq(Comment::getApiId, req.getApiId());
    }

    if(Boolean.TRUE.equals(req.getExcludeMe())){
      queryWrapper.ne(Comment::getUserId, LoginUserHolder.getUserId());
    }

    Page<Comment> page = commentService.page(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_COMMENT),
        queryWrapper
    );
    if(page.getRecords().isEmpty()){
      return PageResp.empty(page);
    }


    //userNickName
    Set<Long> userIdSet = page.getRecords().stream()
        .map(Comment::getUserId)
        .collect(Collectors.toSet());
    Map<Long, User> userMap = userService.listByIds(userIdSet)
        .stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));

    //apiName
    Set<Long> apiIdSet = page.getRecords().stream()
        .map(Comment::getApiId)
        .collect(Collectors.toSet());
    Map<Long, HttpApi> apiMap = apiService.listByIds(apiIdSet)
        .stream()
        .collect(Collectors.toMap(HttpApi::getId, Function.identity()));

    Set<Long> commentIdSet = page.getRecords().stream()
        .map(Comment::getId)
        .collect(Collectors.toSet());

    List<Comment> adminReplyList = commentService.list(
        new LambdaQueryWrapper<Comment>()
            .eq(Comment::getUserId, LoginUserHolder.getUserId())
            .in(Comment::getReplyToCommentId, commentIdSet)
    );
    Map<Long, List<Comment>> adminReplyMap = new HashMap<>();
    for (Comment comment : adminReplyList) {
      adminReplyMap.computeIfAbsent(comment.getReplyToCommentId(),
          k -> new ArrayList<>()).add(comment);
    }

    return Resp.ok(new PageResp<>(
        page.getRecords().stream().map(e->{
          CommentPageVo vo = new CommentPageVo();
          BeanUtils.copyProperties(e, vo);
          vo.setIsRoot(e.getRootCommentId() == CommonConstant.ROOT_COMMENT_ROOT_ID);

          User user = userMap.get(e.getUserId());
          vo.setUserNickname(user.getNickname());
          vo.setUsername(user.getUsername());
          vo.setApiName(apiMap.get(e.getApiId()).getName());

          List<Comment> adminReplies = adminReplyMap.get(e.getId());
          if(adminReplies != null){
            vo.setAdminReplies(adminReplies.stream().map(e2->{
              CommentVo adVo = new CommentVo();
              BeanUtils.copyProperties(e2, adVo);
              return adVo;
            }).collect(Collectors.toList()));
          }

          return vo;
        }).collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize()
    ));

  }
}

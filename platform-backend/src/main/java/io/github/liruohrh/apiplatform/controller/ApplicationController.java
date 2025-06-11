package io.github.liruohrh.apiplatform.controller;

import static io.github.liruohrh.apiplatform.model.enume.ApplicationType.COMMENT_REPORT;
import static io.github.liruohrh.apiplatform.model.enume.ApplicationType.ORDER_FOUND;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.mapper.CommentMapper;
import io.github.liruohrh.apiplatform.model.entity.Application;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.Order;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.ApplicationAuditStatus;
import io.github.liruohrh.apiplatform.model.enume.OrderStatusEnum;
import io.github.liruohrh.apiplatform.model.req.ApplicationAddReq;
import io.github.liruohrh.apiplatform.model.req.ApplicationAuditReq;
import io.github.liruohrh.apiplatform.model.req.application.ListApplicationReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.CommentReportApplicationVo;
import io.github.liruohrh.apiplatform.model.vo.CommentVo;
import io.github.liruohrh.apiplatform.service.ApplicationService;
import io.github.liruohrh.apiplatform.service.CommentService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/application")
@RestController
public class ApplicationController {

  @Resource
  private OrderService orderService;
  @Resource
  private HttpApiService apiService;
  private final UserService userService;
  private final ApplicationService applicationService;
  private final CommentService commentService;
  @Resource
  private CommentMapper commentMapper;

  public ApplicationController(
      UserService userService, ApplicationService applicationService, CommentService commentService
  ) {
    this.userService = userService;
    this.applicationService = applicationService;
    this.commentService = commentService;
  }

  @GetMapping
  public Resp<PageResp<CommentReportApplicationVo>> listCommentReportApplication(ListApplicationReq req) {
    LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<Application>()
        .eq(Application::getApplicationType, COMMENT_REPORT.getValue());

    if(StringUtils.isNotBlank(req.getReporterName())){
      List<Long> userIds = userService.list(new LambdaQueryWrapper<User>()
              .select(User::getId)
              .and(qw->qw
                  .like(User::getNickname, req.getReporterName())
                  .or()
                  .like(User::getUsername, req.getReporterName())
              )
          ).stream()
          .map(User::getId)
          .collect(Collectors.toList());
      if(userIds.isEmpty()){
        return PageResp.empty();
      }
      queryWrapper.in(Application::getReporterId, userIds);
    }
    if(StringUtils.isNotBlank(req.getReason())){
      queryWrapper.like(Application::getReason, req.getReason());
    }
    if(req.getAuditStatus() != null){
      queryWrapper.eq(Application::getAuditStatus, req.getAuditStatus());
    }
    if(CollectionUtil.isNotEmpty(req.getCtimeRange()) && req.getCtimeRange().size() == 2){
      queryWrapper.between(Application::getCtime, req.getCtimeRange().get(0), req.getCtimeRange().get(1));
    }
    queryWrapper.orderBy(req.getCtimeS() != null, Boolean.TRUE.equals(req.getCtimeS()), Application::getCtime);

    Page<Application> page = applicationService.page(
        new Page<>(req.getCurrent(), CommonConstant.PAGE_MAX_SIZE_APPLICATION),
        queryWrapper
    );

    if (page.getRecords().isEmpty()) {
      return PageResp.empty(page);
    }

    List<Comment> reportedCommentList = commentMapper.listByIds(page.getRecords().stream()
        .map(Application::getExtraData)
        .map(Long::parseLong)
        .collect(Collectors.toSet()));
    Map<String, Comment> reportedCommentMap = reportedCommentList
        .stream()
        .collect(Collectors.toMap(e -> e.getId().toString(), Function.identity()));

    Map<Long, HttpApi> apiMap = apiService.list(
        new LambdaQueryWrapper<HttpApi>()
            .select(HttpApi::getId, HttpApi::getName)
            .in(HttpApi::getId, reportedCommentList
                .stream()
                .map(Comment::getApiId)
                .collect(Collectors.toSet()))
        )
        .stream()
        .collect(Collectors.toMap(HttpApi::getId, Function.identity()));

    HashSet<Long> userIds = page.getRecords().stream()
        .map(Application::getReporterId).collect(Collectors.toCollection(HashSet::new));
    userIds.addAll(reportedCommentList.stream().map(Comment::getUserId).collect(Collectors.toList()));

    Map<Long, User> reporterMap = userService.listByIds(userIds)
        .stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));

    return Resp.ok(new PageResp<>(
        page.getRecords().stream()
            .map(e -> {
              CommentReportApplicationVo vo = new CommentReportApplicationVo();
              BeanUtils.copyProperties(e, vo);

              User reporter = reporterMap.get(e.getReporterId());
              vo.setReporterNickname(reporter.getNickname());
              vo.setReporterUsername(reporter.getUsername());

              CommentVo commentVo = new CommentVo();
              Comment reportedComment = reportedCommentMap.get(e.getExtraData());
              BeanUtils.copyProperties(reportedComment, commentVo);
              User reportedUser = reporterMap.get(commentVo.getUserId());
              commentVo.setUserNickName(reportedUser.getNickname());
              commentVo.setUsername(reportedUser.getUsername());
              vo.setReportedComment(commentVo);

              vo.setApiId(reportedComment.getApiId());
              vo.setApiName(apiMap.get(reportedComment.getApiId()).getName());
              return vo;
            }).collect(Collectors.toList()),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize()
    ));
  }
  @Transactional(rollbackFor = Exception.class)
  @PostMapping("/audit/{id}")
  public Resp<Void> auditApplication(
      @PathVariable("id") Long applicationId,
      @Validated @RequestBody ApplicationAuditReq req
  ) {
    Application application = applicationService.getById(applicationId);
    if (application == null) {
      throw new ParamException("applicationId=" + applicationId + " is not exists");
    }
    MustUtils.dbSuccess(applicationService.update(new LambdaUpdateWrapper<Application>()
        .eq(Application::getId, applicationId)
        .set(Application::getAuditStatus, req.getAuditStatus().getValue())
        .set(StringUtils.isNotBlank(req.getReplyContent()), Application::getReplyContent,
            req.getReplyContent())
    ));
    if (COMMENT_REPORT.is(application.getApplicationType())) {
      long commentId = Long.parseLong(application.getExtraData());
      if (ApplicationAuditStatus.APPROVED == req.getAuditStatus()) {
        commentService.removeById(commentId);
      } else {
        commentMapper.recover(commentId);
      }
    }else if(ORDER_FOUND.is(application.getApplicationType())){
      OrderStatusEnum status;
      if (ApplicationAuditStatus.APPROVED == req.getAuditStatus()) {
        status  = OrderStatusEnum.REFUND_SUCCESS;
      } else if(ApplicationAuditStatus.NOT_PASS == req.getAuditStatus()){
        status  = OrderStatusEnum.REFUND_FAIL;
      }else{
        status = OrderStatusEnum.REFUNDING;
      }
      MustUtils.dbSuccess(orderService.update(new LambdaUpdateWrapper<Order>()
          .eq(Order::getOrderId, Long.parseLong(application.getExtraData()))
          .set(Order::getStatus, status.getValue())
      ));
    }
    return Resp.ok();
  }

  @PostMapping("/report/comment")
  public Resp<Void> reportComment(
      @RequestParam("commentId") Long commentId,
      @Validated @RequestBody ApplicationAddReq req
  ) {

    if (!commentService.exists(new LambdaQueryWrapper<Comment>().eq(Comment::getId, commentId))) {
      throw new ParamException("commentId=" + commentId + " is not exists");
    }

    Application application = new Application();
    BeanUtils.copyProperties(req, application);
    application.setReporterId(LoginUserHolder.getUserId());
    application.setExtraData(commentId.toString());

    application.setApplicationType(COMMENT_REPORT.getValue());
    application.setAuditStatus(ApplicationAuditStatus.PENDING_AUDIT.getValue());

    MustUtils.dbSuccess(applicationService.save(application));
    return Resp.ok();
  }
}

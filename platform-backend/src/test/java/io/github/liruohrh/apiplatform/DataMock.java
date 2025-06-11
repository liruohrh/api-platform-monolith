package io.github.liruohrh.apiplatform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.service.ApplicationService;
import io.github.liruohrh.apiplatform.service.CommentInteractionService;
import io.github.liruohrh.apiplatform.service.CommentService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.NotificationService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootTest
public class DataMock {

  @Resource
  HttpApiService apiService;
  @Resource
  UserService userService;
  @Resource
  OrderService orderService;
  @Resource
  CommentService commentService;
  @Resource
  CommentInteractionService commentInteractionService;
  @Resource
  NotificationService notificationService;
  @Resource
  ApplicationService applicationService;
  ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
      .build();

  @Test
  public void testReport() throws IOException {
//    File file = new File("mock/comment_report.json");
//    List<Map<String, Object>> commentReports = objectMapper.readValue(file, ArrayList.class);
//    List<Application> applicationList = commentReports.stream().map(e -> {
//      Application application = new Application();
//      application.setReason(e.get("reason").toString());
//      application.setDescription(e.get("description").toString());
//      application.setReporterId(((Number) (e.get("userId"))).longValue());
//      application.setExtraData(e.get("commentId").toString());
//
//      application.setApplicationType(COMMENT_REPORT.getValue());
//      application.setAuditStatus(ApplicationAuditStatus.PENDING_AUDIT.getValue());
//      return application;
//    }).collect(Collectors.toList());
//    applicationService.saveBatch(applicationList);
  }

  @Test
  public void testCommentReplyCount() {
//    List<Comment> newCommentList = commentService.listMaps(new QueryWrapper<Comment>()
//            .select("reply_to_comment_id", "count(id) as reply_amount")
//            .ne("reply_to_comment_id", 0)
//            .groupBy("reply_to_comment_id")
//        ).stream()
//        .map(e -> {
//          Comment comment = new Comment();
//          comment.setId(((Number) e.get("reply_to_comment_id")).longValue());
//          comment.setReplyCount(((Number) e.get("reply_amount")).intValue());
//          return comment;
//        }).collect(Collectors.toList());
//    commentService.updateBatchById(newCommentList);

  }

  @Test
  public void testComment4() {
//    List<Comment> commentList = commentService.list().stream().map(e -> {
//      long replyCount = commentService.count(new LambdaQueryWrapper<Comment>()
//          .eq(Comment::getReplyToCommentId, e.getId()));
//      Comment comment = new Comment();
//      comment.setId(e.getId());
//      comment.setReplyCount((int) replyCount);
//      return comment;
//    }).collect(Collectors.toList());
//    commentService.updateBatchById(commentList);
  }

  @Test
  public void testComment3() {
//    Instant newCommentMinCtime = Instant.ofEpochMilli(1718044800000L);
//    List<Comment> comments = commentService.list(new LambdaQueryWrapper<Comment>()
//            .lt(Comment::getId, 54)
//            .orderByDesc(Comment::getId)
//        )
//        .stream()
//        .map(e -> {
//          Comment comment = new Comment();
//          comment.setId(e.getId());
//          comment.setCtime(newCommentMinCtime.plus(-5, ChronoUnit.HOURS).toEpochMilli());
//          return comment;
//        }).collect(Collectors.toList());
//    commentService.updateBatchById(comments);

  }

  @Test
  public void testComment2() throws IOException {
//    File file = new File("mock/comment.json");
//    List<Comment> comments = objectMapper.readValue(file,
//        new TypeReference<List<Comment>>() {
//        });
//    commentService.updateBatchById(comments.stream().map(e->{
//      Comment comment = new Comment();
//      comment.setId(e.getId());
//      comment.setCtime(e.getCtime());
//      return comment;
//    }).collect(Collectors.toList()));
  }

  @Test
  public void testComment() throws IOException {
    File file = new File("mock/comment.json");
    List<Comment> comments = objectMapper.readValue(file,
        new TypeReference<List<Comment>>() {
        });
    Random random = new Random();
    comments.forEach(e -> {
      e.setApiId(2L);
//      if (e.getId() >= 59 && e.getId() <= 74) {
//        e.setRootCommentId(54L);
//      } else if (e.getId() >= 75 && e.getId() <= 86) {
//        e.setRootCommentId(55L);
//      } else if (e.getId() >= 87 && e.getId() <= 98) {
//        e.setRootCommentId(56L);
//      } else if (e.getId() >= 99 && e.getId() <= 112) {
//        e.setRootCommentId(57L);
//      } else if (e.getId() >= 113 && e.getId() <= 138) {
//        e.setRootCommentId(58L);
//      }
      if (e.getRootCommentId() == CommonConstant.ROOT_COMMENT_ROOT_ID) {
        e.setScore(random.nextInt(10) + 1);
      }
    });
//    List<Map<String, Object>> jsonMapList = new ArrayList<>(comments.size());
//    comments.forEach(e -> {
//      Map<String, Object> jsonMap = new LinkedHashMap<>();
//      jsonMapList.add(jsonMap);
//      jsonMap.put("id", e.getId());
//      jsonMap.put("rootCommentId", e.getRootCommentId());
//      jsonMap.put("replyToCommentId", e.getReplyToCommentId());
//      jsonMap.put("userId", e.getUserId());
//      jsonMap.put("replyToUserId", e.getReplyToUserId());
//      jsonMap.put("content", e.getContent());
//      jsonMap.put("replyCount", e.getReplyCount());
//      jsonMap.put("ctime", e.getCtime());
//    });
//    Files.write(Paths.get(file.getPath() + ".temp"),
//        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonMapList));
//    commentService.saveBatch(comments);
  }

  @Test
  public void testUser() {
//    User test1 = userService.getOne(new LambdaQueryWrapper<User>()
//        .eq(User::getUsername, "test1"));
//    int size = 15 + 30;
//    ArrayList<User> users = new ArrayList<>();
//    for (int i = 15; i < size; i++) {
//      User user = BeanUtil.copyProperties(test1, User.class);
//      user.setId(null);
//      user.setNickname("test" + i);
//      user.setUsername("test" + i);
//      user.setEmail(user.getUsername() + "@test.com");
//      user.setPersonalDescription("a test " + i);
//
//      user.setAppKey(SecurityUtils.generateAppKey(user.getEmail()));
//      user.setAppSecret(SecurityUtils.generateAppSecret(user.getEmail()));
//
//      user.setCtime(
//          LocalDateTime.now().plusDays(-(i + 1) * 2).toEpochSecond(ZoneOffset.ofHours(8)) * 1000);
//      users.add(user);
//    }
//    userService.saveBatch(users);
  }


  @Test
  public void updateFavorComment() {
//    commentService.list(new LambdaQueryWrapper<Comment>()
//            .ge(Comment::getId, 140)
//            .eq(Comment::getUserId, 3)
//            .eq(Comment::getReplyToUserId, 2)
//        )
//        .forEach(e -> {
////          CommentInteraction interaction = new CommentInteraction();
////          interaction.setCommentId(e.getId());
////          interaction.setUserId(3L);
////          interaction.setFavor(true);
////          commentInteractionService.save(interaction);
//          Notification notification = new Notification();
//          notification.setIsRead(false);
//          notification.setNoticeType(NotificationType.COMMENT.getValue());
//          notification.setUserId(e.getReplyToUserId());
//          notification.setTitle(NotificationType.Comment.REPLY.getValue());
//          notification.setContent(e.getId() + "");
//          MustUtils.dbSuccess(notificationService.save(notification));
//        });
  }

  @Test
  public void updateAPIScore() {
//    apiService.list().forEach(e -> apiService.updateScore(e.getId()));
  }
}

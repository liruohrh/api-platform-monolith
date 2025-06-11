package io.github.liruohrh.apiplatform.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.entity.Comment;
import io.github.liruohrh.apiplatform.model.req.comment.CommentAddReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.CommentVo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentControllerTest extends AbstractControllerTest {
    static Long commentId;
    @Test
    @Order(1)
    void testPostComment() {
        CommentAddReq req = new CommentAddReq();
        req.setApiId(defaultApiId);
        req.setOrderId(defaultOrderId);
        req.setContent("测试评论");

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/comment")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<CommentVo> resp = assertSuccess(response, new TypeReference<Resp<CommentVo>>() {
        });
        assertNotNull(resp.getData());
        commentId = resp.getData().getId();
    }

    @Test
    @Order(2)
    void testGetComment() {
        HttpResponse response = withLoginState(HttpRequest.get(withBaseUrl("/comment/" + defaultCommentId)))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<Comment> resp = assertSuccess(response, new TypeReference<Resp<Comment>>() {
        });
        assertNotNull(resp.getData());
    }

    @Test
    @Order(3)
    void testListComment() {
        HttpResponse response = withLoginState(HttpRequest.get(
                UrlBuilder.of(withBaseUrl("/comment"))
                    .addQuery("apiId", defaultApiId)
                    .addQuery("current", 1)
                    .build()
            ))
            .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<PageResp<CommentVo>> resp = assertSuccess(response, new TypeReference<Resp<PageResp<CommentVo>>>() {
        });
        assertNotNull(resp.getData());
        assertTrue(resp.getData().getData().stream().anyMatch(e->e.getId().equals(commentId)));
    }

    @Test
    @Order(4)
    void testFavorComment() {
        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/comment/" + commentId + "/favor")))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<Boolean> resp = assertSuccess(response, new TypeReference<Resp<Boolean>>() {
        });
        assertNotNull(resp.getData());
        assertTrue(resp.getData());
    }

    @Test
    @Order(5)
    void testDeleteComment() {
        HttpResponse response = withLoginState(HttpRequest.delete(withBaseUrl("/comment/" + commentId)))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<Boolean> resp = assertSuccess(response, new TypeReference<Resp<Boolean>>() {
        });
        assertNotNull(resp.getData());
        assertTrue(resp.getData());
    }
}
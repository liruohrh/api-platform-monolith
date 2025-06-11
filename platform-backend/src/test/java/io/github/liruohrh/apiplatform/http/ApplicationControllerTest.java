package io.github.liruohrh.apiplatform.http;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.entity.Application;
import io.github.liruohrh.apiplatform.model.req.ApplicationAddReq;
import io.github.liruohrh.apiplatform.model.req.ApplicationAuditReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.enume.ApplicationAuditStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationControllerTest extends AbstractControllerTest {

    @Test
    @Order(1)
    void testReportComment() {
        ApplicationAddReq req = new ApplicationAddReq();
        req.setReason("测试举报");
        req.setDescription("这是一个测试举报");

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/application/report/comment"))
                .form("commentId", "1"))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }

    @Test
    @Order(2)
    void testListCommentReportApplication() {
        HttpResponse response = withLoginState(HttpRequest.get(withBaseUrl("/application")))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<PageResp<Application>> resp = assertSuccess(response, new TypeReference<Resp<PageResp<Application>>>() {
        });
        assertNotNull(resp.getData());
    }

    @Test
    @Order(3)
    void testAuditApplication() {
        ApplicationAuditReq req = new ApplicationAuditReq();
        req.setAuditStatus(ApplicationAuditStatus.APPROVED);
        req.setReplyContent("审核通过");

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/application/audit/1")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }
}
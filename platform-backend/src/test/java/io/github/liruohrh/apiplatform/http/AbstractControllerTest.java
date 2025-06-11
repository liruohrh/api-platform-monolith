package io.github.liruohrh.apiplatform.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.service.EmailService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractControllerTest {
    @MockBean(answer = Answers.RETURNS_MOCKS)
    JavaMailSender javaMailSender;
    @Resource
    EmailService emailService;

    @LocalServerPort
    private int port;
    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    private static String baseUrl = null;
    private static String logState;
    // 这些default是数据必定有的值，用来测试，不能被删除，不能作为新增的值，因为这个值已经在数据库存在
    static Long defaultApiId = 1L;
    static Long defaultOrderId = 1L;
    static Long defaultCommentId = 1L;
    static Long defaultUserId = 1L;
    static String defaultUsername = "system";
    static String defaultUserPassword = "123456";
    static User loginUser;

    @BeforeEach
    void setUp() {
//        if (baseUrl != null) {
//            return;
//        }
//
//        baseUrl = "http://localhost:" + port + (contextPath.startsWith("/") ? "" : "/") + contextPath
//                + (contextPath.endsWith("/") ? "" : "/");
//        UserLoginReq req = new UserLoginReq();
//        req.setUsername(defaultUsername);
//        req.setPasswd(defaultUserPassword);
//        req.setLoginType(LoginType.PASSWD);
//        HttpResponse response = HttpRequest.post(withBaseUrl("/auth/login"))
//                .body(JSONUtil.toJsonStr(req))
//                .execute();
//        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
//        Resp<User> resp = JSONUtil.toBean(response.body(), new TypeReference<Resp<User>>() {
//        }, false);
//        assertSuccess(resp);
//        logState = response.getCookieStr();
//        loginUser = resp.getData();
    }

    HttpRequest withLoginState(HttpRequest request) {
        return request.header(Header.COOKIE, logState);
    }

    String withBaseUrl(String path) {
        return baseUrl + (path.startsWith("/") ? path.substring(1) : path);
    }

    <T> Resp<T> assertSuccess(HttpResponse response, TypeReference<Resp<T>> typeReference) {
        Resp<T> resp = JSONUtil.toBean(response.body(), typeReference, false);
        assertSuccess(resp);
        return resp;
    }

    <T> Resp<T> assertSuccessNoForData(HttpResponse response, TypeReference<Resp<T>> typeReference) {
        Resp<T> resp = JSONUtil.toBean(response.body(), typeReference, false);
        assertEquals(ErrorCode.SUCCESS.getCode(), resp.getCode());
        return resp;
    }

    void assertSuccess(HttpResponse response) {
        assertSuccess(JSONUtil.toBean(response.body(), Resp.class));
    }

    void assertSuccess(Resp<?> resp) {
        assertEquals(ErrorCode.SUCCESS.getCode(), resp.getCode());
        assertNotNull(resp.getData());
    }

    void assertFail(HttpResponse response) {
        Resp resp = JSONUtil.toBean(response.body(), Resp.class);
        assertFail(resp);
        StackTraceElement testMethodFrame = new RuntimeException().getStackTrace()[1];
        log.error("Test method: {}.{} failed, response: {}: {}", testMethodFrame.getClassName(),
                testMethodFrame.getMethodName(), resp.getCode(), resp.getMsg());
    }

    void assertFail(Resp<?> resp) {
        assertNotEquals(ErrorCode.SUCCESS.getCode(), resp.getCode());
        assertTrue(resp.getData() == null || resp.getData() instanceof JSONNull);
        assertNotNull(resp.getMsg());
    }
}
package io.github.liruohrh.apiplatform.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.LoginType;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.req.user.UserNewPasswdReq;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest extends AbstractControllerTest {

    @Test
    void testRegisterSuccess() {
        UserRegisterReq req = new UserRegisterReq();
        req.setUsername("testuser" + System.currentTimeMillis());
        req.setNickname(req.getUsername());
        req.setPasswd("123456");
        req.setEmail(req.getUsername() + "@example.com");

        String captcha = emailService.captcha(req.getEmail());
        req.setCaptcha(captcha);
        HttpResponse response = HttpRequest.post(withBaseUrl("/auth/register"))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }

    @Test
    void testRegisterFail_DuplicateUsername() {
        UserRegisterReq req = new UserRegisterReq();
        req.setUsername(loginUser.getUsername());
        req.setPasswd(loginUser.getPasswd());
        req.setEmail(loginUser.getEmail());

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/auth/register")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Test
    void testLoginTwice() {
        UserLoginReq req = new UserLoginReq();
        req.setUsername(defaultUsername);
        req.setPasswd(defaultUserPassword);
        req.setLoginType(LoginType.PASSWD);
        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/auth/login")))
            .body(JSONUtil.toJsonStr(req))
            .execute();
        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Test
    void testLoginFail_WrongPassword() {
        UserLoginReq req = new UserLoginReq();
        req.setUsername(defaultUsername);
        req.setPasswd(defaultUserPassword + RandomUtil.randomString(10));
        HttpResponse response = HttpRequest.post(withBaseUrl("/auth/login"))
            .body(JSONUtil.toJsonStr(req))
            .execute();
        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Test
    void testGetLoginUserSuccess() {
        HttpResponse response = withLoginState(HttpRequest.get(withBaseUrl("/auth")))
                .execute();

        Resp<User> resp = assertSuccess(response, new TypeReference<Resp<User>>() {});
        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertEquals(loginUser.getUsername(), resp.getData().getUsername());
    }

    @Test
    void testGetLoginUserFail_NoToken() {
        HttpResponse response = HttpRequest.get(withBaseUrl("/auth"))
                .execute();
        assertEquals(HttpStatus.HTTP_UNAUTHORIZED, response.getStatus());
        assertFail(response);
    }

    @Test
    void testChangePasswordFail() {
        UserNewPasswdReq req = new UserNewPasswdReq();
        req.setEmail(loginUser.getEmail());
        req.setNewPasswd(RandomUtil.randomString(10));
        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/auth/passwd")))
                .body(JSONUtil.toJsonStr(req))
                .execute();
        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Order(Order.DEFAULT+100)
    @Test
    void testLogoutSuccess() {
        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/auth/logout")))
                .execute();
        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }
}
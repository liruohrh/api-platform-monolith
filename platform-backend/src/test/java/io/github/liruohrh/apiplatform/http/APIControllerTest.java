package io.github.liruohrh.apiplatform.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.req.admin.api.AdminApiSearchReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpMethod;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class APIControllerTest extends AbstractControllerTest {
    static long apiId;
    static String apiName = "测试API" + System.currentTimeMillis();
    @Test
    @Order(1)
    void testAddAPI() {
        HttpApiAddReq req = new HttpApiAddReq();
        req.setName(apiName);
        req.setDescription("这是一个测试API");
        req.setMethod(HttpMethod.GET);
        req.setDomain("http://example.com");
        req.setPath("/test");
        req.setProtocol("HTTP");
        req.setPrice(0.0);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/http-api")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        apiId = assertSuccess(response, new TypeReference<Resp<Long>>() {}).getData();

    }

    @Test
    @Order(2)
    void testGetAPIById() {
        HttpResponse response = withLoginState(HttpRequest.get(withBaseUrl("/http-api/"+apiId)))
                .execute();
        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }

    @Test
    @Order(3)
    void testListAPI() {
        AdminApiSearchReq adminApiSearchReq = new AdminApiSearchReq();
        adminApiSearchReq.setCurrent(1);
        HttpResponse response = withLoginState(HttpRequest.post(
            UrlBuilder.of(withBaseUrl("/http-api/list")).setQuery(new UrlQuery()
                    .addAll(BeanUtil.beanToMap(adminApiSearchReq, false, true)))
                .build()
        ))
                .body(JSONUtil.toJsonStr(adminApiSearchReq))
                .execute();
        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<PageResp<HttpApi>> resp = assertSuccess(response, new TypeReference<Resp<PageResp<HttpApi>>>() {
        });
        assertNotNull(resp.getData().getData());
        assertTrue(resp.getData().getData().stream().anyMatch(e->e.getId().equals(apiId) && e.getName().equals(apiName)));
    }

    @Test
    @Order(4)
    void testUpdateAPI() {
        HttpApiUpdateReq req = new HttpApiUpdateReq();
        req.setId(apiId);
        apiName = "更新后的API" + System.currentTimeMillis();
        req.setName(apiName);
        req.setDescription("这是更新后的描述");
        HttpResponse response = withLoginState(HttpRequest.put(withBaseUrl("/http-api")))
                .body(JSONUtil.toJsonStr(req))
                .execute();
        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);

        AdminApiSearchReq adminApiSearchReq = new AdminApiSearchReq();
        adminApiSearchReq.setName(apiName);
        adminApiSearchReq.setCurrent(1);
        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<PageResp<HttpApi>> resp = assertSuccess(withLoginState(HttpRequest.post(
            UrlBuilder.of(withBaseUrl("/http-api/list")).setQuery(new UrlQuery()
                    .addAll(BeanUtil.beanToMap(adminApiSearchReq, false, true)))
                .build()
        ))
            .execute(), new TypeReference<Resp<PageResp<HttpApi>>>() {
        });
        assertEquals(1, resp.getData().getData().size());
        assertEquals(apiName,  resp.getData().getData().get(0).getName());
    }

    @Test
    @Order(5)
    void testDeleteAPI() {
        HttpResponse deleteResp = withLoginState(HttpRequest.delete(withBaseUrl("/http-api/" + apiId)))
            .execute();
        assertEquals(HttpStatus.HTTP_OK, deleteResp.getStatus());
        assertSuccess(deleteResp);

        HttpResponse byIdResp = withLoginState(HttpRequest.get(withBaseUrl("/http-api/"+apiId)))
            .execute();
        assertEquals(HttpStatus.HTTP_OK, byIdResp.getStatus());
        Resp<HttpApiResp> resp = assertSuccessNoForData(byIdResp,
            new TypeReference<Resp<HttpApiResp>>() {
            });
        assertNull(resp.getData());
    }
}
package io.github.liruohrh.apiplatform.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderCreateReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderRefundReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSearchReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSortReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.OrderVo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest extends AbstractControllerTest {
    static Long freeApiId = 2L;
    static String orderId;
    static String freeOrderId;
    static String freeTimesOrderId;

    @Test
    @org.junit.jupiter.api.Order(1)
    void testCreateFreeOrder() {
        // 创建一个免费API的订单
        OrderCreateReq req = new OrderCreateReq();
        req.setApiId(freeApiId);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<OrderVo> resp = assertSuccess(response, new TypeReference<Resp<OrderVo>>() {
        });
        assertNotNull(resp.getData());
        freeOrderId = resp.getData().getOrderId();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testCreateFreeOrderFail() {
        // 尝试重复创建免费API的订单，应该失败
        OrderCreateReq req = new OrderCreateReq();
        req.setApiId(freeApiId);
        req.setAmount(0);
        req.setFree(false);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testCreateFreeTimesOrder() {
        // 创建一个免费次数的订单
        OrderCreateReq req = new OrderCreateReq();
        req.setApiId(defaultApiId);
        req.setFree(true);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<OrderVo> resp = assertSuccess(response, new TypeReference<Resp<OrderVo>>() {
        });
        assertNotNull(resp.getData());
        freeTimesOrderId = resp.getData().getOrderId();
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testCreateFreeTimesOrderFail() {
        // 尝试重复创建免费次数的订单，应该失败
        OrderCreateReq req = new OrderCreateReq();
        req.setApiId(defaultApiId);
        req.setFree(true);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testCreatePaidOrder() {
        // 创建一个付费订单
        OrderCreateReq req = new OrderCreateReq();
        req.setApiId(defaultApiId);
        req.setAmount(1000); // 根据API价格，这里应该设置合适的数量
        req.setFree(false);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<OrderVo> resp = assertSuccess(response, new TypeReference<Resp<OrderVo>>() {
        });
        assertNotNull(resp.getData());
        orderId = resp.getData().getOrderId();
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testCreatePaidOrderFail() {
        // 尝试创建数量不符合要求的付费订单，应该失败
        OrderCreateReq req = new OrderCreateReq();
        req.setApiId(defaultApiId);
        req.setAmount(5); // 设置一个不符合步长要求的数量
        req.setFree(false);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_BAD_REQUEST, response.getStatus());
        assertFail(response);
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testGetOrderById() {
        HttpResponse response = withLoginState(HttpRequest.get(withBaseUrl("/order"))
                .form("orderId", orderId))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<OrderVo> resp = assertSuccess(response, new TypeReference<Resp<OrderVo>>() {
        });
        assertNotNull(resp.getData());
        assertEquals(orderId, resp.getData().getOrderId());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testListOrder() {
        PageReq<OrderSearchReq, OrderSortReq> req = new PageReq<>();
        OrderSearchReq searchReq = new OrderSearchReq();
        searchReq.setUserId(loginUser.getId());
        req.setSearch(searchReq);

        HttpResponse response = withLoginState(HttpRequest.post(withBaseUrl("/order/list")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        Resp<PageResp<OrderVo>> resp = assertSuccess(response, new TypeReference<Resp<PageResp<OrderVo>>>() {
        });
        assertNotNull(resp.getData());
        assertTrue(resp.getData().getData().stream()
                .anyMatch(order -> order.getOrderId().equals(orderId)));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testPayOrder() {
        HttpResponse response = withLoginState(HttpRequest.put(withBaseUrl("/order"))
                .form("orderId", orderId)
                .form("isPay", true))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testRefundOrder() {
        OrderRefundReq req = new OrderRefundReq();
        req.setOrderId(orderId);
        req.setReason("测试退款");
        req.setDescription("这是一个测试退款申请");

        HttpResponse response = withLoginState(HttpRequest.put(withBaseUrl("/order/refund")))
                .body(JSONUtil.toJsonStr(req))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void testCancelRefund() {
        HttpResponse response = withLoginState(HttpRequest.delete(withBaseUrl("/order/refund/cancel"))
                .form("orderUid", orderId))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void testCancelOrder() {
        testCreatePaidOrder();
        HttpResponse response = withLoginState(HttpRequest.put(withBaseUrl("/order"))
                .form("orderId", orderId)
                .form("isCancel", true))
                .execute();

        assertEquals(HttpStatus.HTTP_OK, response.getStatus());
        assertSuccess(response);
    }
}
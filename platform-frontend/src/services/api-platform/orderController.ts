// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /order */
export async function getOrderById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOrderByIdParams,
  options?: { [key: string]: any },
) {
  return request<API.RespOrderVo>('/order', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /order */
export async function optForOrder(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.optForOrderParams,
  options?: { [key: string]: any },
) {
  return request<API.RespVoid>('/order', {
    method: 'PUT',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order */
export async function createOrder(body: API.OrderCreateReq, options?: { [key: string]: any }) {
  return request<API.RespOrderVo>('/order', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order/admin/list */
export async function listAllOrder(
  body: API.PageReqOrderSearchReqOrderSortReq,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespOrderVo>('/order/admin/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order/list */
export async function listOrder(
  body: API.PageReqOrderSearchReqOrderSortReq,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespOrderVo>('/order/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /order/refund */
export async function refund(body: API.OrderRefundReq, options?: { [key: string]: any }) {
  return request<API.RespVoid>('/order/refund', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /order/refund/cancel */
export async function cancelRefund(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.cancelRefundParams,
  options?: { [key: string]: any },
) {
  return request<API.RespVoid>('/order/refund/cancel', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

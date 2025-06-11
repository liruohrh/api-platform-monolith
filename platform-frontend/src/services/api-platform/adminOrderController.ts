// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /admin/order/api-order/time-range */
export async function apiOrderTimeRange(options?: { [key: string]: any }) {
  return request<API.RespListLong>('/admin/order/api-order/time-range', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /admin/order/status */
export async function listOrderStatus(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listOrderStatusParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespOrderStatusVo>('/admin/order/status', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

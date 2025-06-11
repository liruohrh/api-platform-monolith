// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /admin/statistics/api-order */
export async function apiOrder(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.apiOrderParams,
  options?: { [key: string]: any },
) {
  return request<API.RespApiOrderStatisticsVo>('/admin/statistics/api-order', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

/** 没有year、month表示每年，有year就是指定year的每个月，month就是每天 GET /admin/statistics/api-usage */
export async function apiUsage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.apiUsageParams,
  options?: { [key: string]: any },
) {
  return request<API.RespApiStatusVo>('/admin/statistics/api-usage', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /admin/statistics/api-usage/time-range */
export async function apiUsageTimeRange(options?: { [key: string]: any }) {
  return request<API.RespListLong>('/admin/statistics/api-usage/time-range', {
    method: 'GET',
    ...(options || {}),
  });
}

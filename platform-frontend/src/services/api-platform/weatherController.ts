// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /api/web/weather */
export async function currentWeekWeather(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.currentWeekWeatherParams,
  options?: { [key: string]: any },
) {
  return request<API.RespWeatherResp>('/api/web/weather', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

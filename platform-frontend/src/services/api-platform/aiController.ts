// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /api/web/cold-joke */
export async function coldJoke(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.coldJokeParams,
  options?: { [key: string]: any },
) {
  return request<API.RespString>('/api/web/cold-joke', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

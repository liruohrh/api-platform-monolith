// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 PUT /user/api/app-secret */
export async function resetUserAppSecret(options?: { [key: string]: any }) {
  return request<API.RespVoid>('/user/api/app-secret', {
    method: 'PUT',
    ...(options || {}),
  });
}

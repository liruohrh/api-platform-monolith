// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /admin/api/usage/user */
export async function listUserApiUsage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUserApiUsageParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespUserApiUsageVo>('/admin/api/usage/user', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /admin/api/usage/user/${param0} */
export async function updateUserApiUsage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateUserApiUsageParams,
  body: API.UserApiUsageUpdateReq,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/admin/api/usage/user/${param0}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /admin/api/usage/user/${param0} */
export async function deleteUserApiUsage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteUserApiUsageParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/admin/api/usage/user/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

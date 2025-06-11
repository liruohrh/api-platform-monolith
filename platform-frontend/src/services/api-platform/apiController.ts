// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 PUT /http-api */
export async function updateApi(body: API.HttpApiUpdateReq, options?: { [key: string]: any }) {
  return request<API.RespVoid>('/http-api', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /http-api */
export async function addApi(body: API.HttpApiAddReq, options?: { [key: string]: any }) {
  return request<API.RespLong>('/http-api', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /http-api/${param0} */
export async function getApiById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAPIByIdParams,
  options?: { [key: string]: any },
) {
  const { apiId: param0, ...queryParams } = params;
  return request<API.RespHttpApiResp>(`/http-api/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /http-api/${param0} */
export async function deleteApi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteAPIParams,
  options?: { [key: string]: any },
) {
  const { apiId: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/http-api/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /http-api/${param0}/launch */
export async function launchApi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.launchAPIParams,
  options?: { [key: string]: any },
) {
  const { apiId: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/http-api/${param0}/launch`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /http-api/${param0}/roll-off */
export async function rollOffApi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.rollOffAPIParams,
  options?: { [key: string]: any },
) {
  const { apiId: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/http-api/${param0}/roll-off`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /http-api/all */
export async function listAllApi(options?: { [key: string]: any }) {
  return request<API.RespListHttpApiResp>('/http-api/all', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /http-api/debug/${param0} */
export async function callApi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.callAPIParams,
  options?: { [key: string]: any },
) {
  const { apiId: param0, ...queryParams } = params;
  return request<API.RespApiCallResp>(`/http-api/debug/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /http-api/list */
export async function listApi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listAPIParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespHttpApiResp>('/http-api/list', {
    method: 'POST',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /http-api/purchased */
export async function listUserApiAndUsage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUserApiAndUsageParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespApiAndUsageVo>('/http-api/purchased', {
    method: 'POST',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /http-api/search */
export async function searchApi(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.searchAPIParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespApiSearchVo>('/http-api/search', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

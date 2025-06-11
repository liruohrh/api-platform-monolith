// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /application */
export async function listCommentReportApplication(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listCommentReportApplicationParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespCommentReportApplicationVo>('/application', {
    method: 'GET',
    params: {
      ...params,
      req: undefined,
      ...params['req'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /application/audit/${param0} */
export async function auditApplication(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.auditApplicationParams,
  body: API.ApplicationAuditReq,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/application/audit/${param0}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /application/report/comment */
export async function reportComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.reportCommentParams,
  body: API.ApplicationAddReq,
  options?: { [key: string]: any },
) {
  return request<API.RespVoid>('/application/report/comment', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
    },
    data: body,
    ...(options || {}),
  });
}

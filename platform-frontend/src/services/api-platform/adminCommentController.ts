// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /admin/comment */
export async function listAllComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listAllCommentParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespCommentPageVo>('/admin/comment', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

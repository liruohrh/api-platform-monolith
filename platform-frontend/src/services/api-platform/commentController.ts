// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /comment */
export async function listComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listCommentParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespCommentVo>('/comment', {
    method: 'GET',
    params: {
      ...params,
      commonPageReq: undefined,
      ...params['commonPageReq'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /comment */
export async function postComment(body: API.CommentAddReq, options?: { [key: string]: any }) {
  return request<API.RespCommentVo>('/comment', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /comment/${param0} */
export async function getComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommentParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RespComment>(`/comment/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 DELETE /comment/${param0} */
export async function deleteComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteCommentParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RespBoolean>(`/comment/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /comment/${param0}/favor */
export async function favorComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.favorCommentParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RespBoolean>(`/comment/${param0}/favor`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

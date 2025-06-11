// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /comment/user */
export async function listUserComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUserCommentParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespUserCommentVo>('/comment/user', {
    method: 'GET',
    params: {
      ...params,
      commonPageReq: undefined,
      ...params['commonPageReq'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /comment/user/reply */
export async function listReplyToUserComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listReplyToUserCommentParams,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespCommentReplyVo>('/comment/user/reply', {
    method: 'GET',
    params: {
      ...params,
      commonPageReq: undefined,
      ...params['commonPageReq'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /comment/user/reply */
export async function replyComment(body: API.CommentReplyReq, options?: { [key: string]: any }) {
  return request<API.RespCommentVo>('/comment/user/reply', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /comment/user/reply/count */
export async function getUnReadReplyCount(options?: { [key: string]: any }) {
  return request<API.RespLong>('/comment/user/reply/count', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /comment/user/reply/read */
export async function readReply(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.readReplyParams,
  options?: { [key: string]: any },
) {
  return request<API.RespVoid>('/comment/user/reply/read', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

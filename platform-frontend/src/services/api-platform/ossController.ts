// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /oss */
export async function upload(
  body: {
    file: {
      name?: string;
      size?: number;
      inputStream?: Record<string, any>;
      contentType?: string;
      headerNames?: string[];
      submittedFileName?: string;
    };
  },
  options?: { [key: string]: any },
) {
  return request<string>('/oss', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

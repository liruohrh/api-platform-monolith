// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /api/web/random/emoji */
export async function randomEmoji(options?: { [key: string]: any }) {
  return request<API.RespString>('/api/web/random/emoji', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/web/random/wallpaper */
export async function randomWallpaper(options?: { [key: string]: any }) {
  return request<API.RespString>('/api/web/random/wallpaper', {
    method: 'GET',
    ...(options || {}),
  });
}

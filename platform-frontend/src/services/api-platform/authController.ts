// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /auth */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.RespUser>('/auth', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/login */
export async function login(body: API.UserLoginReq, options?: { [key: string]: any }) {
  return request<API.RespUser>('/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.RespVoid>('/auth/logout', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/passwd */
export async function newUserPasswd(body: API.UserNewPasswdReq, options?: { [key: string]: any }) {
  return request<API.RespVoid>('/auth/passwd', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/register */
export async function register(body: API.UserRegisterReq, options?: { [key: string]: any }) {
  return request<API.RespVoid>('/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

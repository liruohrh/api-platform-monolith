import { getBackendBaseURL } from '@/constants';
import { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import { notification } from 'antd';
// 错误处理方案： 错误类型
enum ErrorShowType {
  SUCCESS = 0,
}

// 与后端约定的响应数据格式
interface ResponseStructure {
  data?: any;
  code: number;
  msg?: string;
}

/**
 * RequestConfig extends AxiosRequestConfig：说明仍然可以用axios的配置
 */
export const requestConfig: RequestConfig = {
  // ...errorConfig,
  baseURL: getBackendBaseURL(),
  timeout: 20000,
  withCredentials: true,
  requestInterceptors: [
    (config: RequestOptions) => {
      // 拦截请求配置，进行个性化处理。
      return config;
    },
  ],
  // 响应拦截器
  responseInterceptors: [
    (response) => {
      // 拦截响应数据，进行个性化处理

      //@ts-ignore
      if (response.config?.skipErrorHandler) {
        return response;
      }
      const { data } = response as unknown as ResponseStructure;
      if (data.code !== ErrorShowType.SUCCESS) {
        // if (response.status === 401) {
        //   notification.info({
        //     message: '未登录，2s后跳转登录页面',
        //   });
        //   setTimeout(() => history.push(loginPath), 2000);
        //   return response;
        // }

        switch (data.code) {
          default:
            //顶部拖出
            // message.error(data.msg);
            //右边拖出，更好，可以设置停留时间
            notification.error({
              message: data.msg,
              duration: 10, //单位：s
            });
            throw new Error(data.msg);
        }
      }
      return response;
    },
  ],
  validateStatus: () => {
    return true;
  },
};

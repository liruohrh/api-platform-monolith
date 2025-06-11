import { resetUserAppSecret } from '@/services/api-platform/userApiController';
import { useModel } from '@@/exports';
import { ProCard } from '@ant-design/pro-components';
import { Button, notification, Space, Typography } from 'antd';
import React from 'react';

const DeveloperCredentials: React.FC = () => {
  console.log("更新 DeveloperCredentials");
  const { initialState, refresh } = useModel('@@initialState');
  const currentUser = initialState!.currentUser;
  if (!currentUser) {
    return <ProCard loading></ProCard>;
  }
  return (
    <ProCard bordered title="开发者凭证" type="inner">
      <Typography.Paragraph>
        <Space direction={'horizontal'}>
          <Typography.Text>AppKey: </Typography.Text>
          <Typography.Text copyable>{currentUser?.appKey}</Typography.Text>
        </Space>
      </Typography.Paragraph>
      <Space direction={'horizontal'}>
        <Typography.Paragraph>
          <Space direction={'horizontal'}>
            <Typography.Text>AppSecret: </Typography.Text>
            <Typography.Text copyable>{currentUser?.appSecret}</Typography.Text>
          </Space>
        </Typography.Paragraph>
        <Button
          onClick={() => {
            resetUserAppSecret().then(() => {
              refresh();
              notification.success({ message: "重置AppSecret成功" })
            });
          }}
        >
          重置
        </Button>
      </Space>
      <Typography.Paragraph type="secondary">
        使用方法：调用API时，必须存在非空的5个请求头：
        <ul>
          <li>appKey: 您的应用密钥</li>
          <li>nonce: 任意随机数</li>
          <li>timestamp: 当前时间戳</li>
          <li>extra: 任意额外数据（推荐使用请求参数）</li>
          <li>sign: 签名，计算方式为 Base64(MD5(appKey + appSecret + nonce + timestamp + extra))</li>
        </ul>
      </Typography.Paragraph>
    </ProCard>
  );
};
export default DeveloperCredentials;

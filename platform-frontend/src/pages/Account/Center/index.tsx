import { ProCard } from '@ant-design/pro-components';
import UserSettings from './components/UserSettings';
import React, { useState } from 'react';
import UserAPIView from './components/UserAPI';
import CommentAndReply from './components/CommentAndReply';
import ReplyToMe from './components/ReplyToMe';
import { useModel } from '@umijs/max';
import { Spin } from 'antd';

const AccountCenter: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const [key, setKey] = useState("UserSettings");

  if (!initialState) {
    return <Spin />;
  }

  return (
    <ProCard
      tabs={{
        activeKey: key,
        size: "large",
        onChange(activeKey) {
          setKey(activeKey);
        },
        type: 'card',
        items: [
          {
            label: "个人设置",
            key: "UserSettings",
            children: <UserSettings />,
            forceRender: false,
            destroyInactiveTabPane: true
          },
          {
            label: "已购买的API",
            key: "UserAPIView",
            children: <UserAPIView />,
            forceRender: false,
            destroyInactiveTabPane: true
          },
          {
            label: "我的评论/回复",
            key: "CommentAndReply",
            children: <CommentAndReply />,
            forceRender: false,
            destroyInactiveTabPane: true
          },
          {
            label: `回复给我的`,
            key: "ReplyToMe",
            children: <ReplyToMe />,
            forceRender: false,
            destroyInactiveTabPane: true,
          }
        ]
      }}
    />
  );
}
export default AccountCenter;

import {
  ProCard,
} from '@ant-design/pro-components';
import { Link, useModel } from '@umijs/max';
import React from 'react';
import UserInfo from '@/pages/Account/Center/components/UserInfo';
import DeveloperCredentials from '@/pages/Account/Center/components/DeveloperCredentials';

const UserSettings: React.FC = () => {
  const { initialState } = useModel("@@initialState");
  return (
    <>
      <ProCard
        bordered
        headerBordered
        direction="column"
        gutter={[0, 16]}
        style={{ marginBlockStart: 8 }}
      >
        <UserInfo />
        <ProCard title="其他设置" type="inner" bordered>
          <Link to={`/user/passwd?email=${initialState?.currentUser?.email ?? ""}`}>修改密码</Link>
        </ProCard>
        <DeveloperCredentials />
      </ProCard>
    </>
  );
};
export default UserSettings;

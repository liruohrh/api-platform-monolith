import { LockOutlined, MailOutlined, UserOutlined } from '@ant-design/icons';
import { ModalForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import '@umijs/max';
import { Space } from 'antd';
import React, { useState } from 'react';


type AddModalProp = {
  open: boolean;
  onOk: (values: API.UserAddReq) => Promise<void>;
  onCancel: () => void;
};
const AddModal: React.FC<AddModalProp> = ({ open, onOk, onCancel }) => {
  const [confirmLoading, setConfirmLoading] = useState<boolean>(true);
  return (
    <ModalForm
      clearOnDestroy
      title={'新增用户'}
      open={open}
      onFinish={async (values) => {
        //点击关闭按钮才会finish
        setConfirmLoading(false);
        try {
          await onOk(values as API.UserAddReq);
        } finally {
          setConfirmLoading(true);
        }
      }}
      onOpenChange={(open) => {
        if (!open) {
          onCancel();
        }
      }}
      modalProps={{
        destroyOnClose: true,
        confirmLoading: confirmLoading
      }}
    >
      <Space direction={"vertical"} style={{ width: "100%" }}>
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <UserOutlined />,
          }}
          name="nickname"
          label={'昵称'}
          required={false}
          rules={[
            {
              min: 1,
              max: 30,
              message: "昵称长度为1-30"
            }
          ]}
        />
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <UserOutlined />,
          }}
          name="username"
          label={'用户名'}
          rules={[
            {
              required: true,
              message: '用户名不能为空',
            },
            {
              pattern: /^\w+$/,
              message: '用户名只能是字母数字下划线',
            },
          ]}
          required={false}
        />
        <ProFormText.Password
          name="passwd"
          fieldProps={{
            size: 'large',
            prefix: <LockOutlined />,
          }}
          label={'密码'}
          rules={[
            {
              required: true,
              message: '密码是必填项！',
            },
            {
              message: '密码长度至少为5',
              min: 5,
            },
          ]}
        />

        <Space direction={"horizontal"} style={{ width: "100%" }} styles={{ item: { flex: 1 } }}>
          <ProFormSelect
            name="role"
            label={'角色'}
            initialValue={"USER"}
            valueEnum={{
              USER: '普通用户',
              ADMIN: '管理员',
            }}
          />
        </Space>
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <MailOutlined />,
          }}
          name="email"
          label={'邮箱'}
          rules={[
            {
              required: true,
              message: '邮箱号是必填项！',
            },
            {
              pattern:
                /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/,
              message: '不合法的邮箱！',
            },
          ]}
          required={false}
        />
      </Space>
    </ModalForm>
  );
};
export default AddModal;

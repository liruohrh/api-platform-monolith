import UserAvatarUpload from '@/components/Upload/UserAvatarUpload';
import { getCaptcha } from '@/services/api-platform/emailController';
import { updateUser } from '@/services/api-platform/userController';
import { useModel } from '@@/exports';
import { InfoCircleOutlined, LockOutlined, MailOutlined, UserOutlined } from '@ant-design/icons';
import {
  CaptFieldRef,
  ProCard,
  ProForm,
  ProFormCaptcha,
  ProFormGroup,
  ProFormInstance,
  ProFormText,
} from '@ant-design/pro-components';
import { Button, notification, Space, Typography } from 'antd';
import React, { useRef } from 'react';
import { getSamePropButValueNotEquals } from '@/utils';
const UserInfo: React.FC = () => {

  const userInfoFormRef = useRef<ProFormInstance>();
  const changeEmailCaptchaRef = useRef<CaptFieldRef>();

  const { initialState, refresh } = useModel('@@initialState');
  const currentUser = initialState!.currentUser;
  if (!currentUser) {
    return <ProCard loading></ProCard>;
  }
  let avatarUrl = currentUser?.avatarUrl;
  const setAvatarUrl = (val: string) => {
    avatarUrl = val;
  };
  const handlerUpdateUserInfo = async () => {
    try {
      const values = await userInfoFormRef.current?.validateFieldsReturnFormatValue!();
      values.avatarUrl = avatarUrl;

      // @ts-ignore
      return updateUser({
        ...getSamePropButValueNotEquals(values, currentUser),
        id: currentUser.id
      }).then((resp) => {
        if (resp.data) {
          refresh();
          notification.success({ message: '更新用户信息成功' });
        }
      });
    } catch (e) {
      // @ts-ignore
      notification.error({ message: e.errorFields.map((f) => f.errors.join(',')).join('\n') });
    }
  };

  return (
    <ProCard
      bordered
      title="个人信息设置"
      type="inner"
      extra={
        <Button type={'primary'} onClick={handlerUpdateUserInfo}>
          确认修改
        </Button>
      }
    >
      <Space direction={'horizontal'}>
        <Typography.Text>头像: </Typography.Text>
        <UserAvatarUpload avatarUrl={avatarUrl} setAvatarUrl={setAvatarUrl} />
      </Space>
      <ProForm submitter={false} formRef={userInfoFormRef}>
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <UserOutlined />,
          }}
          name="nickname"
          label={'昵称'}
          initialValue={currentUser?.nickname}
          required={false}
          rules={[
            {
              min: 1,
              message: "昵称不能为空"
            },
            {
              min: 1,
              max: 30,
              message: "只能输入1-30个字符"
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
          initialValue={currentUser?.username}
          rules={[
            {
              pattern: /^\w+$/,
              message: '用户名只能是字母数字下划线',
            },
            {
              min: 1,
              max: 30,
              message: "只能输入1-30个字符"
            }
          ]}
          required={false}
          disabled={currentUser?.username === "system"}
        />
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <InfoCircleOutlined />,
          }}
          name="personalDescription"
          label={'个人描述'}
          initialValue={currentUser?.personalDescription}
          rules={[
            {
              max: 256,
              message: '个人描述长度最多256',
            },
          ]}
          required={false}
        />
        <ProFormGroup>
          <ProFormText
            fieldProps={{
              size: 'large',
              prefix: <MailOutlined />,
            }}
            name="email"
            label={'邮箱'}
            initialValue={currentUser?.email}
            rules={[
              {
                pattern:
                  /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/,
                message: '不合法的邮箱！',
              },
            ]}
            required={false}
          />
          <ProFormCaptcha
            fieldRef={changeEmailCaptchaRef}
            fieldProps={{
              size: 'large',
              prefix: <LockOutlined />,
            }}
            captchaProps={{
              size: 'large',
            }}
            label={'验证码'}
            tooltip={'修改邮箱时才需要'}
            captchaTextRender={(timing, count) => {
              if (timing) {
                return `${count} ${'秒后重新获取'}`;
              }
              return '获取验证码';
            }}
            name="captcha"
            rules={[
              {
                message: '验证码长度为6',
                len: 6,
              },
            ]}
            required={false}
            onGetCaptcha={async () => {
              try {
                let values = await userInfoFormRef.current!.validateFields(["email"]);
                if (values.errorFields) {
                  notification.error({ message: "无法获取验证码，邮箱错误，请检查邮箱" });
                  setTimeout(() => {
                    changeEmailCaptchaRef.current?.endTiming();
                  }, 500);
                  return;
                }
                if (values.email === currentUser?.email) {
                  notification.warning({ message: "无法获取验证码，邮箱和旧邮箱一样，不需要更新" });
                  setTimeout(() => {
                    changeEmailCaptchaRef.current?.endTiming();
                  }, 500);
                  return;
                }
                await getCaptcha({
                  email: values.email as string,
                });
                notification.success({ message: '请求验证码成功' });
              } catch (e) {
                //@ts-ignore
                if (e.errorFields) {
                  notification.error({ message: "无法获取验证码，邮箱错误，请检查邮箱" });
                  setTimeout(() => {
                    changeEmailCaptchaRef.current?.endTiming();
                  }, 500);
                  return;
                }
              }
            }}
          />
        </ProFormGroup>
      </ProForm>
    </ProCard>
  );
};

export default UserInfo;

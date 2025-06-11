import { Footer } from '@/components';
import { getCaptcha } from '@/services/api-platform/emailController';
import { logout, newUserPasswd } from '@/services/api-platform/authController';
import { history } from '@@/core/history';
import {
  LockOutlined,
  MailOutlined,
} from '@ant-design/icons';
import { LoginForm, ProFormCaptcha, ProFormText } from '@ant-design/pro-components';
import { Helmet, useModel, useParams, useSearchParams } from '@umijs/max';
import { notification } from 'antd';
import { createStyles } from 'antd-style';
import React from 'react';
import Settings from '../../../../config/defaultSettings';
import { loginPath } from '@/constants';
import logo from "@/assets/logo.svg";

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.2)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    },
    lang: {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
    container: {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    },
  };
});
const Passwd: React.FC = () => {
  const { styles } = useStyles();
  const { setInitialState } = useModel("@@initialState");

  const handleSubmit = async (values: API.UserNewPasswdReq) => {
    const resp = await newUserPasswd({
      ...values,
    });
    if (resp.code === 0) {
      notification.success({ message: '重置密码成功,2s后跳转登录页' });
      setInitialState((prev) => ({ ...prev, currentUser: undefined }));
      setTimeout(() => {
        history.push(loginPath);
      }, 2000);
    }
  };
  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {'重置密码'}- {Settings.title}
        </title>
      </Helmet>
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={logo}
          title="API开放平台"
          subTitle={'API开放平台 致力于方便、高效的API'}
          // 要在这里设置初始值，可能是不能2个地方都有
          initialValues={{
            email: new URLSearchParams(window.location.search).get("email") ?? "",
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserNewPasswdReq);
          }}
          submitter={{
            searchConfig: {
              submitText: '重置密码',
            },
          }}
        >
          <ProFormText.Password
            name="newPasswd"
            fieldProps={{
              size: 'large',
              prefix: <LockOutlined />,
            }}
            placeholder={'新密码'}
            rules={[
              {
                required: true,
                message: '新密码是必填项！',
                min: 5,
              },
              {
                message: '密码长度至少为5',
                min: 5,
              },
            ]}
          />
          <ProFormText.Password
            fieldProps={{
              size: 'large',
              prefix: <LockOutlined />,
            }}
            name="passwd2"
            placeholder={'确认密码'}
            rules={[
              {
                required: true,
                message: '确认密码是必填项！',
              },
              ({ getFieldValue }) => ({
                validator(rule, value) {
                  if (!value || getFieldValue('newPasswd') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject('两次密码输入不一致');
                },
              }),
            ]}
          />
          <ProFormText
            fieldProps={{
              size: 'large',
              prefix: <MailOutlined />,
            }}
            name="email"
            placeholder={'请输入邮箱！'}
            rules={[
              {
                required: true,
                message: '邮箱是必填项！',
              },
              {
                pattern:
                  /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/,
                message: '不合法的邮箱！',
              },
            ]}
          />
          <ProFormCaptcha
            fieldProps={{
              size: 'large',
              prefix: <LockOutlined />,
            }}
            captchaProps={{
              size: 'large',
            }}
            phoneName={'email'}
            placeholder={'验证码'}
            captchaTextRender={(timing, count) => {
              if (timing) {
                return `${count} ${'秒后重新获取'}`;
              }
              return '获取验证码';
            }}
            name="captcha"
            rules={[
              {
                required: true,
                message: '验证码是必填项！',
              },
              {
                message: '验证码长度为6',
                len: 6,
              },
            ]}
            onGetCaptcha={async (email) => {
              await getCaptcha({
                email,
              });
              notification.success({ message: '请求验证码成功' });
            }}
          />
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Passwd;

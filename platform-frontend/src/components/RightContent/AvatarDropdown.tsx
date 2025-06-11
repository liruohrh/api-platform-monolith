import { AccountBookOutlined, AuditOutlined, LoginOutlined, LogoutOutlined, RedditOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { history, useAccess, useModel } from '@umijs/max';
import { createStyles } from 'antd-style';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback, useState } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import { logout } from '@/services/api-platform/authController';
import { loginPath } from '@/constants';
import { notification, Spin } from 'antd';

export type GlobalHeaderRightProps = {
  children?: React.ReactNode;
};

export const AvatarName = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};
  if (currentUser) {
    return <span className="anticon">{currentUser.nickname}</span>;
  } else {
    return <span className="anticon"><UserOutlined />游客</span>;
  }
};

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      display: 'flex',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      alignItems: 'center',
      padding: '0 8px',
      cursor: 'pointer',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
  };
});

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ children }) => {
  const access = useAccess();
  const { styles } = useStyles();
  const { initialState, setInitialState } = useModel('@@initialState');

  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await logout();
    const { search, pathname } = window.location;
    history.replace({
      pathname: loginPath,
      search: stringify({
        redirect: pathname + search,
      }),
    });
  };

  const onMenuClick = useCallback(
    async (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout') {
        await loginOut();
        flushSync(() => {
          setInitialState((s) => ({ ...s, currentUser: undefined }));
        });
      } else if (key === 'login') {
        history.push(loginPath);
      }
      else if (key === 'accountCenter') {
        history.push("/account/center");
      } else if (key === 'userOrder') {
        history.push("/order/list");
      } else if (!access.isAdminMode && key === 'adminMode') {
        localStorage.setItem("isAdminMode", "true");
        await setInitialState((prev => {
          console.log("set adminMode", prev?.settings);
          return {
            ...prev,
            settings: {
              ...prev!.settings,
              layout: "side"
            }
          };
        }));
      } else if (access.isAdminMode && key === 'exitAdminMode') {
        localStorage.setItem("isAdminMode", "false");
        if (location.pathname.startsWith("/pages/admin/")) {
          history.push("/");
        }
        await setInitialState((prev => {
          console.log("exit adminMode", prev?.settings);
          return {
            ...prev,
            settings: {
              ...prev!.settings,
              layout: "top"
            }
          };
        }));
      } else {

        notification.error({ message: "未知选项" })
      }
    },
    [setInitialState],
  );

  const loading = (
    <span className={styles.action}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }

  const { currentUser } = initialState;
  if (!currentUser || !currentUser.username) {
    return (
      <HeaderDropdown
        menu={{
          selectedKeys: [],
          onClick: onMenuClick,
          items: [
            {
              key: 'login',
              icon: <LoginOutlined />,
              label: "登录账号",
            },
          ],
        }}
      >
        {children}
      </HeaderDropdown>
    );
  }


  const menuItems = [
    {
      key: 'accountCenter',
      icon: <UserOutlined />,
      label: '个人中心',
    },
    {
      key: 'userOrder',
      icon: <AccountBookOutlined />,
      label: '我的订单',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
    {
      type: 'divider' as const,
    },
  ];

  if (access.canAdmin) {
    if (access.isAdminMode) {
      menuItems.push({
        key: 'exitAdminMode',
        icon: <AuditOutlined />,
        label: '退出管理模式',
      });
    } else {
      menuItems.push({
        key: 'adminMode',
        icon: <AuditOutlined />,
        label: '进入管理模式',
      });
    }
  }



  return (
    <HeaderDropdown
      menu={{
        selectedKeys: [],
        onClick: onMenuClick,
        items: menuItems,
      }}
    >
      {children}
    </HeaderDropdown>
  );
};

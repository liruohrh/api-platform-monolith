import { GithubOutlined, MailOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import { Image, Space, Typography } from 'antd';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'gitee',
          title: (
            <span>
              <GithubOutlined />
              <Typography.Text>支持项目</Typography.Text>
            </span>
          ),
          href: 'https://gitee.com/liruohrh/easy-api',
          blankTarget: true,
        },
        {
          key: '联系平台',
          title: (
            <span>
              <MailOutlined />
              <Typography.Text>联系平台</Typography.Text>
            </span>
          ),
          href: 'https://github.com/liruohrh/easy-api',
          blankTarget: true,
        },
        {
          key: '免责声明',
          title: (
            <span>
              <MailOutlined />
              <Typography.Text>免责声明</Typography.Text>
            </span>
          ),
          href: 'https://github.com/liruohrh/easy-api/用户协议.md#免责声明',
          blankTarget: true,
        },
      ]}
      // @ts-ignore
      copyright={
        <Space direction={'horizontal'}>
          <Typography.Text>{`2024 API开放平台`}</Typography.Text>
          <Typography.Text>|</Typography.Text>
          <Typography.Link href={'https://beian.miit.gov.cn/'}>豫ICP备XXXX号-X</Typography.Link>
          <Typography.Text>|</Typography.Text>
          <Typography.Text>
            <Image src="https://beian.mps.gov.cn/img/logo01.dd7ff50e.png" width={20} height={20} />
            豫公网安备 XXX号
          </Typography.Text>
        </Space>
      }
    />
  );
};

export default Footer;

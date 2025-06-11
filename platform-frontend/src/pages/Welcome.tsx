import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, Space, theme, Typography } from 'antd';
import React from 'react';

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href?: string;
}> = ({ title, href, index, desc }) => {
  const { useToken } = theme;

  const { token } = useToken();

  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      {href && (
        <a href={href} target="_blank" rel="noreferrer">
          了解更多 {'>'}
        </a>
      )}
    </div>
  );
};

const Welcome: React.FC = () => {
  // const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');
  return (
    <PageContainer title={false}>
      <Card
        style={{
          borderRadius: 8,
        }}
        styles={{
          body: {
            backgroundImage:
              initialState?.settings?.navTheme === 'realDark'
                ? 'background-image: linear-gradient(75deg, #1A1B1F 0%, #191C1F 100%)'
                : 'background-image: linear-gradient(75deg, #FBFDFF 0%, #F5F7FF 100%)',
          },
        }}
      >
        <div
          style={{
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage:
              "url('https://gw.alipayobjects.com/mdn/rms_a9745b/afts/img/A*BuFmQqsB2iAAAAAAAAAAAAAAARQnAQ')",
          }}
        >
          <Space direction="vertical" style={{ marginBottom: '1rem' }}>
            <Typography.Title level={1}>欢迎使用 API开放平台 🎉</Typography.Title>
            <Typography.Title level={3}>
              API开放平台是一个为用户和开发者提供全面API接口调用服务的平台 🛠
            </Typography.Title>
            <Typography.Text>
              😀作为用户您可以通过注册登录账户，获取接口调用权限，并根据自己的需求浏览和选择适合的接口。
              您可以在线查看文档、进行接口调试，快速验证接口的功能和效果。
            </Typography.Text>
            <Typography.Text>
              🏁API开放平台都致力于提供稳定、安全、高效的接口调用服务，
              帮助您实现更快速、便捷的开发和调用体验。
            </Typography.Text>
          </Space>
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 16,
            }}
          >
            <InfoCard index={1} title="多样化的接口选择" desc="平台上提供丰富多样的接口供您选择，涵盖了各个领域的功能和服务，满足不同需求。" />
            <InfoCard
              index={2}
              title="在线调试功能"
              desc="您可以在平台上进行查看文档、接口在线调试，快速验证接口的功能和效果，节省了开发调试的时间和工作量。"
            />
            <InfoCard
              index={3}
              title="稳定和安全"
              desc="平台致力于提供稳定和安全的接口调用服务，采用了安全措施和技术手段，保障用户数据的安全性和隐私保护。"
            />
          </div>
        </div>
      </Card>
    </PageContainer>
  );
};

export default Welcome;


import { ProCard } from "@ant-design/pro-components";
import { Card, Cascader, Flex, Image, Input, Modal, Rate, Space, Typography } from "antd";
import { formatTimestamp, jsonPrettify } from "@/utils";
import { BugOutlined } from "@ant-design/icons";
import DebugView from "@/components/DebugView";
import { apiScore } from "@/lib/utils/math";
import AlignLeftText from "@/components/AlignLeftText";
import AlignLeftTexts from "@/components/AlignLeftTexts";

const wordSize = 5;
export type ReadModalProps = {
  handlerCloseModal: () => void;
  open: boolean;
  record: API.HttpApiResp
};
const ReadModal: React.FC<ReadModalProps> = ({
  handlerCloseModal,
  open,
  record,
}) => {
  record.params = jsonPrettify(record?.params, 2);
  record.reqHeaders = jsonPrettify(record?.reqHeaders, 2);
  record.reqBody = jsonPrettify(record?.reqBody);
  record.respHeaders = jsonPrettify(record?.respHeaders, 2);
  record.respBody = jsonPrettify(record?.respBody);
  record.respSuccess = jsonPrettify(record?.respSuccess, 2);
  record.respFail = jsonPrettify(record?.respFail);
  record.errorCodes = jsonPrettify(record?.errorCodes, 2);
  const setEmpty = (val: string | undefined) => {
    return val ? val : "空";
  }


  return (
    <Modal
      title={"查看API详情"}
      open={open}
      onCancel={() => { handlerCloseModal() }}
      onClose={() => { handlerCloseModal() }}
      onOk={() => { handlerCloseModal() }}
      destroyOnClose
      width={"80%"}
    >
      <ProCard>

        <Space direction={'horizontal'} style={{ width: '100%' }}
          size={20}
        >
          <Space direction={'horizontal'}>
            <Typography.Text>LOGO：</Typography.Text>
            <Image src={record.logoUrl} width={100} height={100} />
          </Space>
          <Space direction={"vertical"} style={{ width: '100%', justifyContent: "end" }}>
            <Typography.Title level={4}>
              ID：
              <Typography.Text>
                {record.id}
              </Typography.Text>
            </Typography.Title>
            <Typography.Title level={4}>
              名称：
              <Typography.Text>
                {record.name}
              </Typography.Text>
            </Typography.Title>
          </Space>
          <div>
            <div style={{ width: 50 }}></div>
            <Rate disabled allowHalf value={apiScore(record.score!)} />({apiScore(record.score!)})
          </div>
        </Space>
        <Card title="描述" variant={"borderless"}>
          <Typography.Paragraph>
            {record.description}
          </Typography.Paragraph>
        </Card>
        <Card title="价格" variant={"borderless"}>
          <Typography.Paragraph>
            {record?.price === 0 ? "免费" : "💰" + record?.price?.toFixed(5) + "元"}
          </Typography.Paragraph>
        </Card>
        <Card variant={"borderless"}>
          <AlignLeftTexts
            wordSize={wordSize}
            items={[
              {
                label: "状态",
                children: <>{record.status === 0 && "待审核"}{record.status === 1 && "上线🚀"}{record.status === 2 && "下线⏸"}{record.status === 3 && "被禁用"}</>
              },
              {
                label: "订单量",
                children: record.orderVolume
              },
              {
                label: "创建时间",
                children: formatTimestamp(record.ctime)
              },
              {
                label: "更新时间",
                children: formatTimestamp(record.utime)
              },
            ]}
          />
        </Card>


      </ProCard>

      <Typography.Title level={3}>
        文档
      </Typography.Title>
      <ProCard bordered headerBordered gutter={16}>
        <Input
          addonBefore={
            <Cascader
              disabled
              placeholder={record.method}
              style={{ width: 150 }}
            />}
          readOnly
          defaultValue={`${record.protocol}://${record.domain}${record.path}`}
        />
      </ProCard>
      <ProCard
        tabs={{
          type: 'card',
        }}
      >
        <ProCard.TabPane key="req" tab="请求">
          <ProCard title="请求参数" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.params)}
            </pre>
          </ProCard>
          <ProCard title="请求头" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.reqHeaders)}
            </pre>
          </ProCard>
          <ProCard title="请求体" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.reqBody)}
            </pre>
          </ProCard>
        </ProCard.TabPane>
        <ProCard.TabPane key="resp" tab="响应">

          <ProCard title="响应头" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.respHeaders)}
            </pre>
          </ProCard>
          <ProCard title="响应体" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.respBody)}
            </pre>
          </ProCard>
        </ProCard.TabPane>
        <ProCard.TabPane key="respExample" tab="响应示例">
          <ProCard title="成功响应示例" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.respSuccess)}
            </pre>
          </ProCard>
          <ProCard title="失败响应示例" bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.respFail)}
            </pre>
          </ProCard>
        </ProCard.TabPane>
        <ProCard.TabPane key="errorCodes" tab="错误码">
          <ProCard bordered headerBordered gutter={16}>
            <pre className="prettier-code">
              {setEmpty(record.errorCodes)}
            </pre>
          </ProCard>
        </ProCard.TabPane>

        <ProCard.TabPane
          key="debug"
          tab={
            <Typography.Text>
              <BugOutlined />
              调试
            </Typography.Text>
          }
        >
          <DebugView apiId={record.id!}
            hasParams={record.params ? true : false}
            hasReqHeaders={record.reqHeaders ? true : false}
            hasReqBody={record.reqBody ? true : false}
          />
        </ProCard.TabPane>
      </ProCard>
    </Modal >
  );
};
export default ReadModal;
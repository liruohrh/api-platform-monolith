import { UserAvatarUpload } from "@/components";
import { addApi, updateApi } from "@/services/api-platform/apiController";
import { ModalForm, ProCard, ProFormDigit, ProFormField, ProFormMoney, ProFormSelect, ProFormText, ProFormTextArea } from "@ant-design/pro-components";
import { Button, Flex, Modal, notification, Space, Typography } from "antd";
import { useRef, useState } from "react";
import RespBodySchemaExample from "./example/RespBodySchemaExample";
import RespFailExample from "./example/RespFailExample";
import RespSuccessExample from "./example/RespSuccessExample";
import ParamsSchemaExample from "./example/ParamsSchemaExample";
import ReqHeadersSchemaExample from "./example/ReqHeadersSchemaExample";
import ReqBodySchemaExample from "./example/ReqBodySchemaExample";
import RespHeadersSchemaExample from "./example/RespHeadersSchemaExample";
import ErrorCodesExample from "./example/ErrorCodesExample";
import { jsonPrettify } from "@/utils";


export type UpdateModalFormProps = {
  handlerSubmit: (value: API.HttpApiUpdateReq) => Promise<void>;
  handlerCloseModal: () => void;
  isOpenModal: boolean;
  values: API.HttpApiResp
};

const UpdateModalForm: React.FC<UpdateModalFormProps> = ({
  handlerSubmit,
  handlerCloseModal,
  isOpenModal,
  values,
}) => {
  if (!values) {
    handlerCloseModal();
    return;
  }


  values.params = jsonPrettify(values?.params);
  values.reqHeaders = jsonPrettify(values?.reqHeaders);
  values.reqBody = jsonPrettify(values?.reqBody);
  values.respHeaders = jsonPrettify(values?.respHeaders);
  values.respBody = jsonPrettify(values?.respBody);
  values.respSuccess = jsonPrettify(values?.respSuccess);
  values.respFail = jsonPrettify(values?.respFail);
  values.errorCodes = jsonPrettify(values?.errorCodes);


  const [confirmLoading, setConfirmLoading] = useState<boolean>(true);

  let logoUrl: string | undefined = values ? values.logoUrl : undefined;
  const setLogoUrl = (v: string) => logoUrl = v;
  return (
    <ModalForm
      initialValues={values}
      title={'更新API'}
      open={isOpenModal}
      onFinish={async (values: API.HttpApiUpdateReq) => {
        try {
          values.logoUrl = logoUrl;
          setConfirmLoading(false);
          if (!values.path.startsWith("/")) {
            values.path = "/" + values.path;
          }
          await handlerSubmit(values);
        } finally {
          setConfirmLoading(true);
        }
      }}
      onOpenChange={(open) => {
        if (!open) {
          handlerCloseModal();
        }
      }}
      modalProps={{
        destroyOnClose: true,
        confirmLoading: confirmLoading,
        width: '80%',
      }}
    >
      <ProFormText
        name="id"
        hidden
      />
      <Space direction={'horizontal'}>
        <Typography.Text>LOGO：</Typography.Text>
        <UserAvatarUpload
          avatarUrl={logoUrl}
          setAvatarUrl={setLogoUrl}
        />
      </Space>
      <ProFormText
        name="name"
        label={'名称'}
        rules={[
          {
            required: true,
            message: "API名称不能为空"
          },
          {
            min: 1,
            message: '名称不能为空',
          },
        ]}
      />
      <ProFormSelect
        name="status"
        label={'状态'}
        options={[
          {
            label: "上线🚀",
            value: 1,
          },
          {
            label: "下线⏸",
            value: 2,
          }
        ]}
      />
      <ProFormTextArea
        name="description"
        label={'API描述'}
        rules={[
          {
            max: 1024,
            message: 'API描述长度最多1024',
          },
        ]}
        required={false}
      />

      <ProFormMoney
        tooltip="可以的话，免费更好😊"
        label="价格"
        name="price"
        fieldProps={{ precision: 5, prefix: "💰" }}
        customSymbol={" "}
        rules={[
          {
            required: true,
            message: "API价格不能为空"
          },
          {
            validator: (_: any, value?: number) => {
              if (value === undefined || value === null) {
                return Promise.resolve();
              }
              if (value < 0) {
                return Promise.reject("必须大于0");
              }
              const errorMessage = "大于0.01时，如果小数精度大于2，第3位小数开始必须为0；小于0.01时，只能有一个非0数字";
              let valueStr = (value + "");
              if (value === 0.01) {
                return Promise.resolve();
              } else if (value > 0.01) {
                //整数，不考虑
                if (value.toString().indexOf(".") === -1) {
                  return Promise.resolve();
                }
                //小数
                let fractionalPart = valueStr.substring(valueStr.indexOf(".") + 1);
                //没有3位小数，不考虑
                if (fractionalPart.length <= 2) {
                  return Promise.resolve();
                }
                //3位小数，如1.000  1.002  0.013 0.0123
                fractionalPart = fractionalPart.substring(2);
                for (let ch of fractionalPart.split("")) {
                  if (ch !== "0") {
                    return Promise.reject(errorMessage);
                  }
                }
              }
              //0.001 0.0001  0.0012 
              let noZeroCount = 0;
              for (let ch of valueStr.substring(valueStr.indexOf(".") + 1).split("")) {
                if (ch !== "0") {
                  noZeroCount++;
                  if (noZeroCount > 1) {
                    return Promise.reject(errorMessage);
                  }
                }
              }
              return Promise.resolve();
            }
          }
        ]}
      />
      <ProFormDigit
        tooltip="免费不需要填写😊"
        label="免费次数"
        name="freeTimes"
        width="sm"
        fieldProps={{ style: { width: "100%" } }}
        rules={[
          {
            required: true,
            message: "API免费次数不能为空"
          },
        ]}
      />
      <ProFormSelect
        name="method"
        label={'方法'}
        required={false}
        valueEnum={{
          "GET": {
            text: 'GET',
            status: 'default',
          },
          "POST": {
            text: 'POST',
            status: 'default',
          },
          "PUT": {
            text: 'PUT',
            status: 'default',
          },
          "DELETE": {
            text: 'DELETE',
            status: 'default',
          },
          "TRACE": {
            text: 'TRACE',
            status: 'default',
          }
        }}
      />
      <ProFormSelect
        name="protocol"
        label={'协议'}
        valueEnum={{
          "http": {
            text: 'http',
            status: 'warning',
          },
          "https": {
            text: 'https',
            status: 'success',
          }
        }}
      />
      <ProFormText
        name="domain"
        label={'域名'}
        tooltip={"可以包含端口"}
        rules={[
          {
            required: true,
            message: "API domain不能为空"
          },
          {
            pattern: /(^((\d{1,3}\.){3}\d{1,3})|^((\w+\.)+\w+))(:\d{1,5})?$/,
            message: "非法domain"
          },
        ]}
      />
      <ProFormText
        name="path"
        label={'路径'}
        tooltip={"以/开头"}
        rules={[
          {
            required: true,
            message: "API path不能为空"
          },
          {
            pattern: /^\/[\w/]+/,
            message: "非法path"
          },
        ]}
      />
      <Typography.Title level={5}>
        文档
      </Typography.Title>
      <ProCard
        tabs={{
          type: 'card',
        }}
      >
        <ProCard.TabPane key="params" tab="请求params">

          <ProFormTextArea

            name="params"
            required={false}
          />


          <ParamsSchemaExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="reqHeaders" tab="请求headers">
          <ProFormTextArea

            name="reqHeaders"
            required={false}
          />
          <ReqHeadersSchemaExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="reqBody" tab="请求body">
          <ProFormTextArea

            name="reqBody"
            required={false}
          />
          <ReqBodySchemaExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="respHeaders" tab="响应headers">
          <ProFormTextArea

            name="respHeaders"
            required={false}
          />
          <RespHeadersSchemaExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="respBody" tab="响应body">
          <ProFormTextArea

            name="respBody"
            required={false}
          />

          <RespBodySchemaExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="respSuccess" tab="成功响应示例">
          <ProFormTextArea

            name="respSuccess"
            required={false}
          />

          <RespSuccessExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="respFail" tab="错误响应示例">
          <ProFormTextArea

            name="respFail"
            required={false}
          />
          <RespFailExample />
        </ProCard.TabPane>
        <ProCard.TabPane key="errorCodes" tab="错误码">
          <ProFormTextArea

            name="errorCodes"
            required={false}
          />
          <ErrorCodesExample />
        </ProCard.TabPane>
      </ProCard>
    </ModalForm >
  );
};
export default UpdateModalForm;
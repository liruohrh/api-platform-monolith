import { UserAvatarUpload } from "@/components";
import { ModalForm, ProCard, ProFormDigit, ProFormMoney, ProFormSelect, ProFormText, ProFormTextArea } from "@ant-design/pro-components";
import { Button, message, Space, Typography } from "antd";
import { useRef, useState } from "react";
import RespBodySchemaExample from "./example/RespBodySchemaExample";
import RespFailExample from "./example/RespFailExample";
import RespSuccessExample from "./example/RespSuccessExample";
import ParamsSchemaExample from "./example/ParamsSchemaExample";
import ReqHeadersSchemaExample from "./example/ReqHeadersSchemaExample";
import ReqBodySchemaExample from "./example/ReqBodySchemaExample";
import RespHeadersSchemaExample from "./example/RespHeadersSchemaExample";
import ErrorCodesExample from "./example/ErrorCodesExample";


export type AddModalFormProps = {
    handlerSubmit: (value: API.HttpApiAddReq) => Promise<void>;
    handlerCloseModal: () => void;
    isOpenModal: boolean;
};
const AddModalForm: React.FC<AddModalFormProps> = ({
    handlerSubmit,
    handlerCloseModal,
    isOpenModal,
}) => {
    const [confirmLoading, setConfirmLoading] = useState<boolean>(true);
    const formRef = useRef();
    let logoUrl: string | undefined = undefined;
    const setLogoUrl = (v: string) => logoUrl = v;
    return (
        <ModalForm
            initialValues={{
                price: 0,
                method: "GET",
                params: "",
                reqHeaders: "",
                reqBody: "",
                respHeaders: "",
                respBody: "",
                respSuccess: "",
                respFail: "",
                errorCodes: "",
            }}

            title={'新增API'}
            open={isOpenModal}
            onFinish={async (values: API.HttpApiAddReq) => {
                try {
                    const url = `${values.protocol}://${values.domain}/${values.path}`;
                    if (!url.match(/^https?:\/\/(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b(?:[-a-zA-Z0-9()!@:%_\+.~#?&\/\/=]*)$/)) {
                        message.error("不合法的URL，" + url);
                        return false;
                    }
                    values.logoUrl = logoUrl;
                    //点击关闭按钮才会finish
                    setConfirmLoading(false);
                    if (!values.path.startsWith("/")) {
                        values.path = "/" + values.path;
                    }
                    await handlerSubmit(values);
                    //删除 add时保留的信息

                    //@ts-ignore
                    formRef.current?.resetFields();
                } finally {
                    setConfirmLoading(true);
                }
            }}
            onOpenChange={(open) => {
                //点击取消、确认、关闭按钮都会设置open=false
                if (!open) {
                    handlerCloseModal();
                }
            }}
            formRef={formRef}
            modalProps={{
                destroyOnClose: false,
                confirmLoading: confirmLoading,
                width: '80%',
            }}
        >
            <Space style={{ width: "100%", justifyContent: "end" }}>
                <Button onClick={() => {
                    //@ts-ignore
                    formRef.current?.resetFields();
                }}>清空</Button>
            </Space>
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
            <ProFormTextArea
                name="description"
                label={'API描述'}
                rules={[
                    {
                        required: true,
                        message: "API描述不能为空"
                    },
                    {
                        max: 1024,
                        message: 'API描述长度最多1024',
                    },
                ]}
            />

            <ProFormMoney
                tooltip="可以的话，免费更好😊"
                label="价格(调用一次的)"
                name="price"
                fieldProps={{ precision: 5, prefix: "💰" }}
                customSymbol={" "}
                rules={[
                    {
                        required: true,
                        message: "API价格不能为空"
                    },
                ]}
            />
            <ProFormDigit
                tooltip="免费不需要填写😊"
                label="免费次数"
                name="freeTimes"
                width="sm"
                fieldProps={{ style: { width: "100%" } }}
                required={false}
            />
            <ProFormSelect
                name="method"
                label={'方法'}
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
                    }
                }}
            />

            <ProFormSelect
                name="protocol"
                label={'协议'}
                valueEnum={{
                    "http": {
                        text: 'http',
                        status: 'default',
                    },
                    "https": {
                        text: 'https',
                        status: 'default',
                    }
                }}
            />
            <ProFormText
                name="domain"
                label={'域名'}
            />
            <ProFormText
                name="path"
                label={'路径'}
            />
            <ProCard
                tabs={{
                    type: 'card',
                }}
                title={"参数与示例"}
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
        </ModalForm>
    );
};
export default AddModalForm;
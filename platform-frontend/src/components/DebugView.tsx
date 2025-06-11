import { loginPath } from "@/constants";
import { callApi } from "@/services/api-platform/apiController";
import { base64ToUtf8 } from "@/utils";
import { ProCard, ProForm, ProFormText, ProFormTextArea } from "@ant-design/pro-components";
import { history, useModel } from "@umijs/max";
import { Badge, Button, message, notification, Space, Spin, Typography } from "antd";
import { createRef, useRef, useState } from "react";

const DebugView: React.FC<{
    apiId: number,
    hasParams: boolean,
    hasReqHeaders: boolean,
    hasReqBody: boolean,
}> = ({
    apiId,
    hasParams,
    hasReqHeaders,
    hasReqBody
}) => {
        const { initialState } = useModel("@@initialState");
        const [apiResp, setApiResp] = useState<API.ApiCallResp>({});
        const [isLoding, setLoding] = useState(false);
        const formRef = createRef();
        return (
            <>
                <Space
                    direction={"vertical"}
                    style={{ width: '100%' }}
                >
                    <ProForm
                        submitter={false}
                        formRef={formRef}
                        initialValues={{
                            apiId: apiId,
                            params: "",
                            // 模拟请求
                            headers: "",
                            body: "",
                        }}
                        onFinish={async (values) => {
                            try {
                                setLoding(true);


                                const data = await callApi({
                                    apiId: values.apiId,
                                    ...JSON.parse(values.params ? values.params : "{}")
                                }, {
                                    headers: {
                                        ...JSON.parse(values.headers ? values.body : "{}")
                                    },
                                    body: {
                                        ...JSON.parse(values.body ? values.body : "{}")
                                    }
                                });

                                if (data.data) {
                                    //@ts-ignore
                                    const contentType = data.data.headers["Content-Type"];
                                    if (contentType && contentType.includes("application/json") && data.data.body) {
                                        data.data.body = JSON.parse(
                                            base64ToUtf8(data.data.body)
                                        );
                                    }
                                    setApiResp(data.data);
                                }
                            } catch (e: any) {


                            } finally {
                                setLoding(false);
                            }
                        }}
                    >
                        <ProFormText name="apiId" hidden />
                        <ProCard bordered headerBordered gutter={16}

                            style={{ alignItems: "center" }}
                        >
                            {
                                isLoding ? <Spin /> : <Button
                                    onClick={() => {
                                        if (!initialState?.currentUser) {
                                            history.push(loginPath);
                                            return;
                                        }
                                        console.log(formRef.current);
                                        formRef.current?.submit()
                                    }}
                                >🚀发送</Button>
                            }

                        </ProCard>
                        <ProCard title="请求参数" bordered headerBordered gutter={16}>
                            <ProCard
                                tabs={{
                                    type: 'card',
                                }}
                            >
                                {
                                    hasParams && <ProCard.TabPane key="params" tab="params">
                                        <ProFormTextArea
                                            name="params"
                                            required={false}
                                        />
                                    </ProCard.TabPane>
                                }
                                {
                                    hasReqHeaders && <ProCard.TabPane key="headers" tab="headers">
                                        <ProFormTextArea
                                            name="headers"
                                            required={false}
                                        />
                                    </ProCard.TabPane>
                                }

                                {
                                    hasReqBody && <ProCard.TabPane key="body" tab="body">
                                        <ProFormTextArea
                                            name="body"
                                            required={false}
                                        />
                                    </ProCard.TabPane>
                                }
                            </ProCard>
                        </ProCard>
                    </ProForm>

                    <ProCard loading={isLoding} title="响应体" bordered headerBordered gutter={16}>
                        <Space direction={"vertical"}>
                            <Typography.Paragraph style={{ textAlign: "center" }}>
                                响应码：<Badge
                                    status={apiResp.status ? (apiResp.status >= 400 ? "error" : "success") : "default"}
                                    text={apiResp.status ? apiResp.status : "暂无数据"}
                                />
                            </Typography.Paragraph>


                            <ProCard
                                tabs={{
                                    type: 'card',
                                }}
                            >
                                <ProCard.TabPane key="body" tab="body">
                                    <pre
                                        className="prettier-code"
                                    >
                                        {apiResp.body && Object.keys(apiResp.body).length !== 0
                                            ?
                                            JSON.stringify(apiResp.body, null, 2)
                                            : "暂无数据"
                                        }
                                    </pre>
                                </ProCard.TabPane>
                                <ProCard.TabPane key="headers" tab="headers">
                                    <pre
                                        className="prettier-code"
                                    >
                                        {apiResp.headers && Object.keys(apiResp.headers).length !== 0
                                            ?
                                            JSON.stringify(apiResp.headers, null, 2)
                                            : "暂无数据"
                                        }
                                    </pre>
                                </ProCard.TabPane>
                            </ProCard>
                        </Space>
                    </ProCard>
                </Space>
            </>
        )
    }
export default DebugView;
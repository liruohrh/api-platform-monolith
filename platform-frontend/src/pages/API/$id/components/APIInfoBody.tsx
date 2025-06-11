import DebugView from '@/components/DebugView'
import { BugOutlined } from '@ant-design/icons'
import { ProCard } from '@ant-design/pro-components'
import { Input, Typography, Cascader } from 'antd'
import React from 'react'

export interface APIInfoBodyProps {
    apiInfo: API.HttpApiResp
}
const setEmpty = (val: string | undefined) => {
    return val ? val : "空";
}
export default function APIInfoBody({
    apiInfo
}: APIInfoBodyProps) {

    return (
        <ProCard split={"horizontal"}>
            <ProCard bordered headerBordered gutter={16}>
                <Input
                    addonBefore={
                        <Cascader
                            disabled
                            placeholder={apiInfo.method}
                            style={{ width: 150 }}
                        />}
                    readOnly
                    defaultValue={`${apiInfo.protocol}://${apiInfo.domain}${apiInfo.path}`}
                />
            </ProCard>
            <ProCard
                tabs={{
                    type: 'card',
                    items: [
                        {
                            key: "req",
                            label: "请求",
                            children: <>
                                <ProCard title="请求参数" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.params)}
                                    </pre>
                                </ProCard>
                                <ProCard title="请求头" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.reqHeaders)}
                                    </pre>
                                </ProCard>
                                <ProCard title="请求体" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.reqBody)}
                                    </pre>
                                </ProCard>
                            </>
                        },
                        {
                            key: "resp",
                            label: "响应",
                            children: <>
                                <ProCard title="响应头" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.respHeaders)}
                                    </pre>
                                </ProCard>
                                <ProCard title="响应体" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.respBody)}
                                    </pre>
                                </ProCard>
                            </>
                        },
                        {
                            key: "respExample",
                            label: "响应示例",
                            children: <>
                                <ProCard title="成功响应示例" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.respSuccess)}
                                    </pre>
                                </ProCard>
                                <ProCard title="失败响应示例" bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.respFail)}
                                    </pre>
                                </ProCard>
                            </>
                        },
                        {
                            key: "errorCodes",
                            label: "错误码",
                            children: <>
                                <ProCard bordered headerBordered gutter={16}>
                                    <pre className="prettier-code">
                                        {setEmpty(apiInfo.errorCodes)}
                                    </pre>
                                </ProCard>
                            </>,
                        },
                        {
                            key: "debug",
                            label: <Typography.Text>
                                <BugOutlined />
                                调试
                            </Typography.Text>,
                            children: <>
                                <DebugView apiId={apiInfo.id!}
                                    hasParams={apiInfo.params ? true : false}
                                    hasReqHeaders={apiInfo.reqHeaders ? true : false}
                                    hasReqBody={apiInfo.reqBody ? true : false}
                                />
                            </>
                        }
                    ]
                }}
            />
        </ProCard>
    )
}

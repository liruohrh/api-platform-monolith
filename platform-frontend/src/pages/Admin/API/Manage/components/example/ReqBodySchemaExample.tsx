import { ProFormField } from "@ant-design/pro-components";

const ReqBodySchemaExample: React.FC = () => {
    return (
        <>
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    code: {
                        type: "number",
                        desc: "响应码"
                    },
                    data: {
                        required: true,
                        type: "boolean",
                        desc: "是否成功",
                    },
                    msg: {
                        required: true,
                        type: "string",
                        desc: "响应码"
                    }
                }, null, 4)}
                label={'基础类型示例'}
                valueType={"jsonCode"}
                mode={"read"}
                fieldProps={{
                    style: {
                        width: '100%',
                    },
                }}
            />
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    code: {
                        type: "number",
                        desc: "响应码"
                    },
                    data: {
                        type: "object",
                        desc: "宠物",
                        nestedType: {
                            name: {
                                type: "string",
                                desc: "名字",
                            },
                            species: {
                                type: "string",
                                desc: "种族",
                            }
                        }
                    },
                    msg: {
                        type: "string",
                        desc: "响应码"
                    }
                }, null, 4)}
                label={'嵌套对象示例'}
                valueType={"jsonCode"}
                mode={"read"}
                fieldProps={{
                    style: {
                        width: '100%',
                    },
                }}
            />
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    code: {
                        type: "number",
                        desc: "响应码"
                    },
                    data: {
                        type: "array",
                        desc: "宠物",
                        nestedType: {
                            name: {
                                type: "string",
                                desc: "名字",
                            },
                            species: {
                                type: "string",
                                desc: "种族",
                            }
                        }
                    },
                    msg: {
                        type: "string",
                        desc: "响应码"
                    }
                }, null, 4)}
                label={'嵌套数组示例'}
                valueType={"jsonCode"}
                mode={"read"}
                fieldProps={{
                    style: {
                        width: '100%',
                    },
                }}
            />
        </>
    )
}
export default ReqBodySchemaExample;
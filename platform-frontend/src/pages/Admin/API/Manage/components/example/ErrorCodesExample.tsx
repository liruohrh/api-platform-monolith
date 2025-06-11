import { ProFormField } from "@ant-design/pro-components";

const ErrorCodesExample: React.FC = () => {
    return (
        <>
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    4000: "客户端参数错误"
                }, null, 4)}
                label={'示例'}
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
export default ErrorCodesExample;
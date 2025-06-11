import { ProFormField } from "@ant-design/pro-components";

const RespHeadersSchemaExample: React.FC = () => {
    return (
        <>
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    ContentType: {
                        option: true,
                        desc: "xxx"
                    }
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
export default RespHeadersSchemaExample;
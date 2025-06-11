import { ProFormField } from "@ant-design/pro-components";

const ParamsSchemaExample: React.FC = () => {
    return (
        <>
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    name: {
                        type: "string",
                        required: true,
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
export default ParamsSchemaExample;
import { ProFormField } from "@ant-design/pro-components";

const RespSuccessExample: React.FC = () => {
    return (
        <>
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    code: 0,
                    data: { name: "ok" },
                    msg: "success"
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
export default RespSuccessExample;
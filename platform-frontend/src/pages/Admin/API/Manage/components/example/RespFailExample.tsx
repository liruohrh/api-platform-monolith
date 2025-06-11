import { ProFormField } from "@ant-design/pro-components";

const RespFailExample: React.FC = () => {
    return (
        <>
            <ProFormField
                hideInForm
                text={JSON.stringify({
                    code: 50010,
                    data: null,
                    msg: "some error hanppen"
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
export default RespFailExample;
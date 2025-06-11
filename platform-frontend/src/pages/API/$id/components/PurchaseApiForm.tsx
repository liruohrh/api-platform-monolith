import { isFreeUsed } from "@/services/api-platform/apiUsageController";
import { ModalForm, ProFormDigit } from "@ant-design/pro-components";
import { Card, Checkbox, message, Space, Spin, Typography } from "antd";
import { useEffect, useState } from "react";


function getMinAmount(price: number) {
    if (price <= 0.00009) {
        return 1000;
    } else if (price <= 0.0009) {
        return 100;
    } else if (price <= 0.009) {
        return 10;
    } else {
        return 1;
    }
}
const PurchaseApiModal: React.FC<{
    open: boolean;
    apiId: number;
    price: number;
    freeTimes: number;
    onCancel: () => void;
    onOK: (values: API.OrderCreateReq) => Promise<void>;
}> = ({
    open,
    apiId,
    price,
    freeTimes,
    onCancel,
    onOK
}) => {
        const stepAmount = getMinAmount(price);
        const [amount, setAmount] = useState(stepAmount);
        const [totalPay, setTotalPay] = useState((amount * price).toFixed(2));

        useEffect(() => {
            setTotalPay((amount * price).toFixed(2));
        }, [amount]);

        const [isPurchaseFreeTimes, setPurchaseFreeTimes] = useState(false);
        const [loading, setLoading] = useState(true);
        const [freeUsed, setFreeUsed] = useState<boolean>();


        useEffect(() => {
            isFreeUsed({
                apiId: apiId
            }).then((resp) => {
                if (resp.code !== 0) {
                    message.error("无法获取用户使用情况，请重试");
                    onCancel();
                    return;
                }
                setLoading(false);
                setFreeUsed(resp.data);
            });
        }, []);

        if (loading) {
            return <Spin />;
        }


        return (
            <ModalForm
                modalProps={{
                    destroyOnClose: true
                }}
                open={open}
                onOpenChange={(v) => {
                    if (!v) {
                        onCancel();
                    }
                }}
                onFinish={async (values) => {
                    if (isPurchaseFreeTimes) {
                        onOK({
                            apiId: apiId,
                            free: isPurchaseFreeTimes,
                        });
                    } else {
                        onOK({
                            apiId: apiId,
                            amount: amount
                        });
                    }
                }}
            >
                <Space direction={"vertical"} size={20} style={{ width: "100%" }}>
                    <Typography.Title level={3}>价格：每次{price.toFixed(5)}元</Typography.Title>
                    <Card>
                        {
                            !freeUsed && <Space direction={"horizontal"} size={30}>
                                <Typography>免费次数：{freeTimes}次</Typography>
                                <Checkbox name="free" onChange={(e) => setPurchaseFreeTimes(e.target.checked)}>
                                    购买免费次数
                                </Checkbox>
                            </Space>
                        }
                        {
                            freeUsed && <>
                                <Typography.Paragraph>
                                    免费次数已购买
                                </Typography.Paragraph>
                            </>
                        }
                    </Card>
                    {
                        !isPurchaseFreeTimes && <Card>
                            <ProFormDigit
                                label={<div style={{ verticalAlign: "text-bottom" }}>
                                    <span>数量(次)</span>
                                    <span style={{
                                        paddingLeft: "1rem",
                                        fontSize: "12px",
                                    }}
                                    >说明：购买数量得是{stepAmount}的整数倍。如{stepAmount}次、{2 * stepAmount}次、{3 * stepAmount}次</span>
                                </div>}
                                name="amount"
                                min={stepAmount}
                                fieldProps={{
                                    value: amount,
                                    required: true,
                                    step: stepAmount,
                                    defaultValue: stepAmount,
                                    onChange(value) {
                                        if (value) {
                                            let v = value;
                                            if (value % stepAmount !== 0) {
                                                let n = (value + stepAmount) / stepAmount;
                                                console.log(1, n);
                                                n = Number((n).toFixed(0));
                                                console.log(2, n);
                                                v = n * stepAmount;
                                                console.log(`${n} * ${stepAmount} = ${v}`);
                                            }
                                            setAmount(v);
                                        }
                                    },
                                }}
                            />
                            <Typography.Paragraph>
                                共需支付：{totalPay}元
                            </Typography.Paragraph>
                        </Card>
                    }
                </Space>
            </ModalForm>
        );
    }
export default PurchaseApiModal;
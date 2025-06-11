
import { ORDER_STATUS } from "@/lib/utils/order";
import { getApiById } from "@/services/api-platform/apiController";
import { getOrderById, optForOrder } from "@/services/api-platform/orderController";
import { AlipayOutlined } from "@ant-design/icons";
import { ProCard } from "@ant-design/pro-components";
import { history, useParams } from "@umijs/max";
import { Space, Typography, QRCode, Button, notification } from "antd";
import { useEffect, useState } from "react";

const OrderPayPage: React.FC = () => {
    const params = useParams();
    const [isLoading, setLoading] = useState(true);
    const [orderInfo, setOrderInfo] = useState<API.OrderVo>({});
    const [apiInfo, setApiInfo] = useState<API.HttpApiResp>({});
    useEffect(() => {
        let load = true;
        getOrderById({ orderId: params.id! })
            .then(async (resp) => {
                if (!load) {
                    return;
                }

                if (resp.data) {
                    if (resp.data.status !== ORDER_STATUS.WAIT_PAY) {
                        notification.warning({ message: `异常订单状态(${resp.data.status})。 无法支付！！！` });
                        setTimeout(() => {
                            history.push(`/order/${params.id}`);
                        }, 1000)
                        return;
                    }
                    const data = await getApiById({ apiId: resp.data.apiId! });
                    if (data.data) {
                        setApiInfo(data.data);
                    }
                    setOrderInfo(resp.data);
                    setLoading(false);
                }
            });
        return () => {
            load = false;
        }
    }, []);

    return <>
        <ProCard loading={isLoading} bordered boxShadow>
            <Space direction={"vertical"} style={{ width: "100%", height: "100%" }}>
                <ProCard title="商品信息" bordered boxShadow>
                    <Typography.Paragraph>购买接口{"  "}
                        <Typography.Text strong>{apiInfo.name}</Typography.Text>
                        {"  "}
                        的
                        {" "}
                        <Typography.Text strong>{orderInfo.amount}</Typography.Text>
                        {" "}
                        次使用次数
                    </Typography.Paragraph>
                    <Typography.Paragraph>
                        单价：{orderInfo.price?.toFixed(5)} 元
                    </Typography.Paragraph>
                    <Typography.Paragraph>
                        需要支付：{orderInfo.price! * orderInfo.amount!} 元
                    </Typography.Paragraph>
                    <Typography.Paragraph>订单编号：{orderInfo.orderId}</Typography.Paragraph>
                </ProCard>

                <ProCard title="支付方式" bordered boxShadow style={{ margin: "10px 0" }}>
                    <Space direction={"horizontal"} style={{ justifyContent: "center", width: "100%" }}>
                        <Typography.Paragraph>
                            <AlipayOutlined />
                            <Typography.Text>
                                阿里支付
                            </Typography.Text>
                        </Typography.Paragraph>
                    </Space>

                </ProCard>


                <ProCard title="支付二维码" bordered boxShadow style={{ margin: "10px 0" }}>
                    <Space direction={"horizontal"} style={{ justifyContent: "center", width: "100%" }}>
                        <QRCode value={"https://ant.design/"} />
                    </Space>
                </ProCard>
                <ProCard style={{ margin: "10px 0" }}>
                    <Space direction={"horizontal"} style={{ justifyContent: "center", width: "100%" }}>
                        <Button onClick={async () => {
                            optForOrder({ orderId: params.id!, isPay: true })
                                .then(() => {
                                    notification.success({ message: "支付成功" });

                                    history.push(`/order/${params.id}`);

                                });

                        }}>确认支付</Button>
                    </Space>
                </ProCard>
            </Space >
        </ProCard >
    </>
};
export default OrderPayPage;
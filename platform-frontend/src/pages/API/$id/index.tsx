
import { getApiById } from "@/services/api-platform/apiController";
import { createOrder } from "@/services/api-platform/orderController";
import { jsonPrettify } from "@/utils";
import { ProCard } from "@ant-design/pro-components";
import { history, useModel, useParams } from "@umijs/max";
import { Space, Typography, Image, Card, Button, Modal, notification, Menu, Rate, Spin } from "antd";
import { useEffect, useState } from "react";
import PurchaseApiModal from "./components/PurchaseApiForm";
import APIInfoBody from "./components/APIInfoBody";
import { CommentOutlined, FileTextOutlined } from "@ant-design/icons";
import APICommentList from "./components/APICommentList";
import { loginPath } from "@/constants";
import { apiScore } from "@/lib/utils/math";



const APIDetailPage: React.FC = () => {
    const { initialState } = useModel("@@initialState");
    const params = useParams();
    const [isLoading, setLoading] = useState(true);
    const [apiInfo, setApiInfo] = useState<API.HttpApiResp>();

    const [purchaseApiModalVisible, setPurchaseApiModalVisible] = useState(false);


    useEffect(() => {
        let load = true;
        getApiById({ apiId: parseInt(params.id!) })
            .then(resp => {
                if (!load) {
                    return;
                }

                if (resp.data) {
                    const values = resp.data;
                    values.params = jsonPrettify(values?.params, 2);
                    values.reqHeaders = jsonPrettify(values?.reqHeaders, 2);
                    values.reqBody = jsonPrettify(values?.reqBody);
                    values.respHeaders = jsonPrettify(values?.respHeaders, 2);
                    values.respBody = jsonPrettify(values?.respBody);
                    values.respSuccess = jsonPrettify(values?.respSuccess, 2);
                    values.respFail = jsonPrettify(values?.respFail);
                    values.errorCodes = jsonPrettify(values?.errorCodes, 2);
                    setApiInfo(values);
                }
            }).finally(() => {
                setLoading(false);
            })
        return () => {
            load = false;
        }
    }, []);



    const handlerDoingPurchase = () => {
        if (!initialState?.currentUser) {
            history.push(loginPath);
            return;
        }
        if (apiInfo!.price === 0) {
            createOrder({
                apiId: apiInfo!.id!!
            }).then(resp => {
                notification.success({
                    duration: 10,
                    message: "是否跳转到订单页面？",
                    btn: <Space>
                        <Button type="primary" size="small" onClick={() => {
                            notification.destroy();
                            history.push(`/order/${resp.data?.orderId!!}`);
                        }}>
                            确认
                        </Button>
                        <Button size="small" onClick={() => notification.destroy()}>
                            取消
                        </Button>
                    </Space>
                });
            });
        } else {
            setPurchaseApiModalVisible(true);
        }
    }

    if (isLoading) {
        return <Spin />
    }

    if (!isLoading && !apiInfo) {
        history.replace("/404");
        return <></>;
    }

    return (
        <>
            <ProCard>
                <Space direction={"vertical"} style={{ width: "100%", height: "100%" }}>
                    <ProCard>
                        <Space direction={'horizontal'} style={{ width: '100%', justifyContent: "space-between" }}>
                            <Space direction={'horizontal'} style={{ width: '100%' }}
                                size={20}
                            >
                                <Space direction={'horizontal'}>
                                    <Typography.Text>LOGO：</Typography.Text>
                                    <Image src={apiInfo!.logoUrl} width={100} height={100} />
                                </Space>
                                <Space direction={"vertical"} style={{ width: '100%', justifyContent: "end" }}>
                                    <Typography.Title level={4}>
                                        名称：
                                        <Typography.Text>
                                            {apiInfo!.name}
                                        </Typography.Text>
                                    </Typography.Title>
                                    <Space direction={"horizontal"}>
                                        <Rate allowHalf disabled defaultValue={apiScore(apiInfo!.score!)} />
                                        <span>({apiScore(apiInfo!.score!)})</span>
                                    </Space>
                                </Space>
                            </Space>
                        </Space>
                        <Card title="描述" variant={"borderless"}>
                            <Typography.Paragraph>
                                {apiInfo!.description}
                            </Typography.Paragraph>
                        </Card>
                        <Card title="价格" variant={"borderless"}>
                            <Space direction={"vertical"} size={"middle"}>
                                <Typography.Paragraph>
                                    {apiInfo!.price === 0 ? "免费" : "💰" + apiInfo!.price?.toFixed(5) + "元"}
                                </Typography.Paragraph>
                                <Button
                                    style={{

                                    }}
                                    type="primary"
                                    onClick={handlerDoingPurchase}
                                >
                                    {apiInfo!.price === 0 ? "免费" : ""}购买
                                </Button>
                            </Space>
                        </Card>
                    </ProCard>
                    <ProCard split={"vertical"}>
                        <ProCard colSpan="15%" style={{
                            position: "sticky",
                            top: 0,
                        }}>
                            <Menu
                                onClick={(key) => {
                                    document.getElementById(key.key)?.scrollIntoView();
                                }}
                                defaultSelectedKeys={["docs"]}
                                items={[
                                    {
                                        label: "文档",
                                        key: "docs",
                                        icon: <FileTextOutlined />
                                    },
                                    {
                                        label: "评论",
                                        key: "comments",
                                        icon: <CommentOutlined />
                                    }
                                ]}
                            />
                        </ProCard>
                        <ProCard>
                            <div id="docs">
                                <APIInfoBody apiInfo={apiInfo!} />
                            </div>
                            <div id="comments">
                                <APICommentList apiId={apiInfo!.id!} />
                            </div>
                        </ProCard>
                    </ProCard>
                </Space>
            </ProCard>
            {
                purchaseApiModalVisible && <PurchaseApiModal
                    open={purchaseApiModalVisible}
                    apiId={apiInfo!.id!!}
                    price={apiInfo!.price!!}
                    freeTimes={apiInfo!.freeTimes!!}
                    onCancel={() => {
                        setPurchaseApiModalVisible(false);
                    }}
                    onOK={async (values: API.OrderCreateReq) => {
                        const resp = await createOrder(values);
                        if (resp.code !== 0) {
                            return;
                        }
                        setPurchaseApiModalVisible(false);
                        notification.success({
                            duration: 10,
                            message: "是否跳转到订单页面？",
                            btn: <Space>
                                <Button type="primary" size="small" onClick={() => {
                                    notification.destroy();
                                    history.push(`/order/${resp.data?.orderId!!}`);
                                }}>
                                    确认
                                </Button>
                                <Button size="small" onClick={() => notification.destroy()}>
                                    取消
                                </Button>
                            </Space>
                        });
                    }}
                />
            }
        </>
    );
};
export default APIDetailPage;

import AlignLeftText from "@/components/AlignLeftText";
import AlignLeftTexts from "@/components/AlignLeftTexts";
import OrderStatusTag from "@/components/OrderStatusTag";
import ReFundModal from "@/components/ReFundModal";
import { canCancel, canCancelRefund, canComment, canPlay, canRefund, REFUND_REASONS } from "@/lib/utils/order";
import { postComment } from "@/services/api-platform/commentController";
import { cancelRefund, getOrderById, optForOrder, refund } from "@/services/api-platform/orderController";
import { formatTimestamp } from "@/utils";
import { ProCard } from "@ant-design/pro-components";
import { history, Link, useModel, useParams } from "@umijs/max";
import { Space, Typography, Image, Card, Badge, Button, notification, Modal, Spin, Rate, message } from "antd";
import TextArea from "antd/lib/input/TextArea";
import { useEffect, useState } from "react";
const wordSize = 6;
const OrderDetailPage: React.FC = () => {
    const { initialState } = useModel("@@initialState");
    const params = useParams();
    const [isCommentModalOpen, setIsCommentModalOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [orderInfo, setOrderInfo] = useState<API.OrderVo>({});
    const [commentScore, setCommentScore] = useState(0);
    const [commentContent, setCommentContent] = useState("");

    const isOrderOwner = initialState?.currentUser!.id === orderInfo.userId;

    const [reFundModalVisible, setReFundModalVisible] = useState(false);

    const fetchOrder = () => {
        return getOrderById({ orderId: params.id! });
    }

    useEffect(() => {
        let load = true;
        fetchOrder()
            .then(resp => {
                if (!load) {
                    return;
                }
                if (resp.data) {
                    setOrderInfo(resp.data);
                    setIsLoading(false);
                }
            });
        return () => {
            load = false;
        }
    }, []);

    if (isLoading) {
        return <Spin />
    }

    return <>
        <ProCard>
            <Space direction={"vertical"} style={{ width: "100%", height: "100%" }}>
                <ProCard>
                    <Card variant={"borderless"}>
                        {!orderInfo.api && <Typography.Paragraph type="danger">API被删除</Typography.Paragraph>}
                        {orderInfo.api && <Space direction={"vertical"} style={{ width: "100%" }}>
                            <Link to={`/api/info/${orderInfo.apiId}`}>
                                <Space direction={"horizontal"} style={{ width: '100%', justifyContent: "start" }}>
                                    <Image src={orderInfo.api!.logoUrl} width={100} height={100} />
                                    <Typography.Title level={4}>
                                        {orderInfo.api!.name}
                                    </Typography.Title>
                                </Space>
                            </Link>
                            <Card style={{ width: "100%" }}>
                                <Typography.Paragraph>
                                    描述：
                                </Typography.Paragraph>
                                <Typography.Paragraph>
                                    {orderInfo.api!.description}
                                </Typography.Paragraph>
                            </Card>
                        </Space>}
                    </Card>
                    <Card variant={"borderless"}>
                        <AlignLeftText
                            wordSize={wordSize}
                            label={"订单编号"}
                            strong
                        >
                            {orderInfo.orderId}
                        </AlignLeftText>
                    </Card>
                    <Card variant={"borderless"}>
                        <Space direction={"vertical"} size={"large"}>
                            <AlignLeftTexts
                                wordSize={wordSize}
                                items={[
                                    {
                                        label: "用户",
                                        children: `${orderInfo.userNickname}@${orderInfo.username}`
                                    },
                                    {
                                        label: "实际支付",
                                        children: orderInfo.actualPayment
                                    },
                                    {
                                        label: "单价",
                                        children: orderInfo.price?.toFixed(5)
                                    },
                                    {
                                        label: "购买数量",
                                        children: orderInfo.amount
                                    },
                                    {
                                        label: "是否已使用",
                                        children: orderInfo.isUsed ? "是" : "否"
                                    },
                                    {
                                        label: "支付状态",
                                        children: <OrderStatusTag status={orderInfo.status!} />
                                    },
                                    {
                                        label: "创建时间",
                                        children: formatTimestamp(orderInfo.ctime)
                                    },
                                    {
                                        label: "更新时间",
                                        children: formatTimestamp(orderInfo.utime)
                                    }
                                ]}
                            />
                        </Space>
                    </Card>
                    {
                        orderInfo.application && <Card title={"退款信息"}>
                            <Typography.Paragraph>
                                退款时间： {formatTimestamp(orderInfo.application?.ctime)}
                            </Typography.Paragraph>
                            <Typography.Paragraph>
                                退款原因： {REFUND_REASONS.find(e => e.value === orderInfo.application?.reason)?.label}
                            </Typography.Paragraph>
                            <Typography.Paragraph>
                                详细描述： {orderInfo.application?.description?.length !== 0
                                    ? orderInfo.application.description
                                    : REFUND_REASONS.find(e => e.value === orderInfo.application?.reason)?.description
                                }
                            </Typography.Paragraph>
                            <Typography.Paragraph>
                                审核回复： {orderInfo.application?.replyContent}
                            </Typography.Paragraph>
                        </Card >
                    }
                    {
                        isOrderOwner &&
                        <Card variant={"borderless"}>
                            <Space direction={"horizontal"} style={{ width: "100%", justifyContent: "center" }}>
                                {
                                    canPlay(orderInfo.status!)
                                        ? <Link to={`/order/pay/${orderInfo.orderId}`} >
                                            <Button>
                                                前往支付
                                            </Button>
                                        </Link>
                                        :
                                        <Button
                                            type={"dashed"}
                                            disabled
                                        >
                                            前往支付
                                        </Button>
                                }
                                <Button
                                    type={!canCancel(orderInfo.status!) ? "dashed" : "primary"}
                                    disabled={!canCancel(orderInfo.status!)}
                                    onClick={async () => {
                                        optForOrder({ orderId: params.id!, isCancel: true })
                                            .then(() => {
                                                notification.success({ message: "取消成功" });
                                                history.push(`/order/list`);
                                            });
                                    }}
                                >
                                    取消订单
                                </Button>
                                <Button
                                    type={orderInfo.isUsed || !canRefund(orderInfo.status!, orderInfo.actualPayment!) ? "dashed" : "primary"}
                                    disabled={orderInfo.isUsed || !canRefund(orderInfo.status!, orderInfo.actualPayment!)}
                                    onClick={async () => {
                                        setReFundModalVisible(true);
                                    }}
                                >
                                    退款
                                </Button>
                                <Button
                                    type={orderInfo.isUsed || !canCancelRefund(orderInfo.status!) ? "dashed" : "primary"}
                                    disabled={orderInfo.isUsed || !canCancelRefund(orderInfo.status!)}
                                    onClick={async () => {
                                        const resp = await cancelRefund({
                                            orderUid: orderInfo.orderId!
                                        });
                                        if (resp.code !== 0) {
                                            message.error("取消退款失败");
                                            return;
                                        }
                                        fetchOrder()
                                            .then(resp => {
                                                if (resp.code === 0 && resp.data && orderInfo.status !== resp.data.status) {
                                                    setOrderInfo(resp.data);
                                                }
                                            });
                                        message.success("取消退款成功");
                                    }}
                                >
                                    取消退款
                                </Button>
                                <Button
                                    type={!canComment(orderInfo.status!) ? "dashed" : "primary"}
                                    disabled={orderInfo.isComment || !canComment(orderInfo.status!)}
                                    onClick={async () => {
                                        setIsCommentModalOpen(true);
                                    }}
                                >
                                    {orderInfo.isComment ? "已评论" : canComment(orderInfo.status!) ? "评论" : "订单状态无法评论"}
                                </Button>
                            </Space>
                        </Card>
                    }
                </ProCard>
            </Space>
        </ProCard>

        {
            reFundModalVisible && <ReFundModal
                open={reFundModalVisible}
                onCancel={() => {
                    setReFundModalVisible(false);
                }}
                onOk={async (values: Omit<API.OrderRefundReq, 'orderId'>): Promise<void> => {
                    const resp = await refund({
                        orderId: orderInfo!.orderId!,
                        reason: values.reason,
                        description: values.description ?? ""
                    });
                    if (resp.code === 0) {
                        fetchOrder()
                            .then(resp => {
                                if (resp.code === 0 && resp.data && orderInfo.status !== resp.data.status) {
                                    setOrderInfo(resp.data);
                                }
                            });
                        notification.success({ message: "退款申请成功" });
                        setReFundModalVisible(false);
                    }
                }}
            />
        }

        <Modal
            title={`填写订单评论`}
            open={isCommentModalOpen}
            onOk={async () => {
                try {
                    const resp = await postComment({
                        apiId: orderInfo.apiId!,
                        orderId: orderInfo.id!,
                        score: commentScore * 2,
                        content: commentContent,
                    });
                    if (resp.code !== 0) {
                        return;
                    }
                    notification.success({
                        message: "评论成功"
                    });
                    setOrderInfo({
                        ...orderInfo,
                        isComment: true
                    });
                    setIsCommentModalOpen(false);
                    setCommentScore(0);
                    setCommentContent("");
                } finally {
                }
            }}
            onCancel={() => {
                setIsCommentModalOpen(false);
                setCommentScore(0);
                setCommentContent("");
            }}
            okText="确认"
            cancelText="取消"
        >
            <Space direction={"vertical"} size={"large"} style={{ width: "100%", paddingTop: 30, paddingBottom: 50 }}>
                <Rate allowHalf onChange={(v) => { setCommentScore(v) }} />
                <TextArea showCount
                    maxLength={500}
                    style={{
                        width: "100%"
                    }}
                    onChange={(e) => {
                        setCommentContent(e.target.value);
                    }}
                />
            </Space>
        </Modal >
    </>
};
export default OrderDetailPage;
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProCard, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Card, Divider, message, notification, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import { emptyValueToNull, formatTimestamp, rangeToOrderNumber, } from '@/utils';
import { cancelRefund, listOrder, optForOrder, refund } from '@/services/api-platform/orderController';
import { Link } from '@umijs/max';
import { DateTimeFormatShower, RangeNumberDescription } from '@/components';
import { canCancel, canCancelRefund, canComment, canPlay, canRefund, ORDER_STATUS_ANT_VALUE_ENUM } from '@/lib/utils/order';
import OrderStatusTag from '@/components/OrderStatusTag';
import { mappingSort } from '@/lib/utils/ant';
import ReFundModal from '@/components/ReFundModal';


const PAGE_SIZE = 10;

const OrderListPage: React.FC = () => {

    const [current, setCurrent] = useState<number>(1);
    const actionRef = useRef<ActionType>();

    const [currentRecord, setCurrentRecord] = useState<API.OrderVo>();
    const [reFundModalVisible, setReFundModalVisible] = useState(false);

    const columns: ProColumns<API.OrderVo>[] = [
        {
            title: 'ID',
            dataIndex: 'id',
            valueType: 'text',
            search: false,
            hideInTable: true
        },
        {
            title: '订单编号',
            dataIndex: 'orderId',
            valueType: 'text',
            width: 170,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.orderId}
                </div>
            },
        },
        {
            title: 'API名称',
            dataIndex: 'apiName',
            valueType: 'text',
            width: 200,
            render: (dom, entity, index, action, schema) => {
                return <Link
                    to={`/api/info/${entity.apiId}`}
                    style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}
                >{dom}</Link>
            },
        },
        {
            title: '实际支付',
            dataIndex: 'actualPayment',
            valueType: 'text',
            sorter: true,
            search: {
                transform: (value: string, namePath: string): any => {
                    return {
                        [namePath]: rangeToOrderNumber(value)
                    }
                }
            },
            width: 150,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.actualPayment}
                </div>
            },
        },
        {
            title: '数量',
            dataIndex: 'amount',
            valueType: 'text',
            sorter: true,
            search: false,
            width: 100,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.amount}
                </div>
            },
        },
        {
            title: '单价',
            dataIndex: 'price',
            valueType: 'text',
            sorter: true,
            width: 150,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.price?.toFixed(5)}
                </div>
            },
        },
        {
            title: '支付状态',
            dataIndex: 'status',
            width: 150,
            valueEnum: ORDER_STATUS_ANT_VALUE_ENUM,
            render(dom, record, index, action, schema) {
                return <OrderStatusTag status={record.status!} />
            },
        },
        {
            title: '有无评价',
            dataIndex: 'isComment',
            valueType: 'text',
            sorter: false,
            search: false,
            width: 100,
            render(dom, entity, index, action, schema) {
                if (entity.isComment === true) {
                    return "有";
                }
                if (!canComment(entity.status!)) {
                    return "无法评价";
                }
                return "无";
            },
        },
        {
            title: '是否被使用',
            dataIndex: 'isUsed',
            valueType: 'text',
            sorter: false,
            search: false,
            width: 100,
            render(dom, entity, index, action, schema) {
                if (entity.isUsed === true) {
                    return "是";
                }
                return "否";
            },
        },
        {
            title: '创建时间',
            dataIndex: 'ctime',
            valueType: 'text',
            sorter: true,
            width: 150,
            render(dom, entity, index, action, schema) {
                return formatTimestamp(entity.ctime);
            },
            search: {
                transform: (value: string, namePath: string): any => {
                    return {
                        [namePath]: rangeToOrderNumber(value)
                    }
                }
            }
        },
        // {
        //     title: '更新时间',
        //     dataIndex: 'utime',
        //     valueType: 'text',
        //     sorter: true,
        //     render(dom, entity, index, action, schema) {
        //         return formatTimestamp(entity.ctime);
        //     },
        //     search: {
        //         transform: (value: string, namePath: string): any => {
        //             return {
        //                 [namePath]: rangeToOrderNumber(value)
        //             }
        //         }
        //     }
        // },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            width: 150,
            render: (_, record) => {
                return (
                    <>
                        <Link to={`/order/${record.orderId}`}
                        >
                            <Tag>
                                查看
                            </Tag>
                        </Link>
                        {canPlay(record.status!) && <Link to={`/order/pay/${record.orderId}`}>
                            <Tag>
                                支付
                            </Tag>
                        </Link>}
                        {canCancel(record.status!) &&
                            <Tag
                                style={{ cursor: 'pointer' }}
                                onClick={() => {
                                    optForOrder({ orderId: record.orderId!, isCancel: true })
                                        .then(() => {
                                            notification.success({ message: "取消成功" });
                                            actionRef.current?.reload();

                                        });
                                }}
                            >
                                取消
                            </Tag>
                        }
                        {!record.isUsed && canRefund(record.status!, record.actualPayment!) &&
                            <Tag
                                style={{ cursor: 'pointer' }}
                                onClick={() => {
                                    setCurrentRecord(record);
                                    setReFundModalVisible(true);
                                }}
                            >
                                退款
                            </Tag>
                        }
                        {!record.isUsed && canCancelRefund(record.status!) &&
                            <Tag
                                style={{ cursor: 'pointer' }}
                                onClick={async () => {
                                    const resp = await cancelRefund({
                                        orderUid: record.orderId!
                                    });
                                    if (resp.code !== 0) {
                                        message.error("取消退款失败");
                                        return;
                                    }
                                    message.success("取消退款成功");
                                    actionRef.current?.reload();
                                }}
                            >
                                取消退款
                            </Tag>
                        }
                    </>
                );
            },
        }
    ];

    return (
        <ProCard>
            <Card>
                <RangeNumberDescription />
                <DateTimeFormatShower />
            </Card>
            <Divider />
            <ProTable<API.OrderVo>
                pagination={{
                    // 必须有这2个，虽然最终会根据响应的数组长度重新设置，但是pageSize不可以任意
                    pageSize: PAGE_SIZE,
                    current: current
                }}
                headerTitle={'订单列表'}
                actionRef={actionRef}
                rowKey="id"
                cardBordered
                search={{
                    labelWidth: 120,
                }}
                columns={columns.map(e => ({
                    ...e,
                    align: "center",
                }))}
                //@ts-ignore
                request={async (params, sort, filter) => {
                    const current = params.current ? params.current : 1;
                    const size = params.pageSize;
                    delete params.current;
                    delete params.pageSize;
                    try {
                        const data: API.RespPageRespUserVo = await listOrder({
                            current,
                            size,
                            sort: mappingSort(sort),
                            //@ts-ignore
                            search: emptyValueToNull(params)
                        });
                        setCurrent(current);

                        return {
                            data: data.data?.data,
                            success: true,
                            total: data.data?.total,
                        };
                    } catch (e) {
                        return {
                            success: false,
                        };
                    }
                }}
            />

            {
                reFundModalVisible && <ReFundModal
                    open={reFundModalVisible}
                    onCancel={() => {
                        setReFundModalVisible(false);
                    }}
                    onOk={async (values: Omit<API.OrderRefundReq, 'orderId'>): Promise<void> => {
                        const resp = await refund({
                            orderId: currentRecord!.orderId!,
                            reason: values.reason,
                            description: values.description ?? ""
                        });
                        if (resp.code === 0) {
                            notification.success({ message: "退款申请成功" });
                            actionRef.current?.reload();
                            setReFundModalVisible(false);
                        }
                    }}
                />
            }
        </ProCard>
    );
};
export default OrderListPage;

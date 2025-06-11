import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Card, Divider, message, Select, Tag } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { emptyValueToNull, formatTimestamp, rangeToOrderNumber, } from '@/utils';
import { listAllOrder } from '@/services/api-platform/orderController';
import { Link } from '@umijs/max';
import { listAllApi } from '@/services/api-platform/apiController';
import { DateTimeFormatShower, RangeNumberDescription } from '@/components';
import { mappingSort } from '@/lib/utils/ant';
import { canAuditRefund, ORDER_STATUS_ANT_VALUE_ENUM } from '@/lib/utils/order';
import AuditReFundModal from './components/AuditReFundModal';
import { auditApplication } from '@/services/api-platform/applicationController';
import OrderStatusTag from '@/components/OrderStatusTag';

const PAGE_SIZE = 10;
interface OrderFilters {
    apiId?: number;
}

const OrderAdminListPage: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [apis, setApis] = useState<API.HttpApiResp[]>([]);
    const [filters, setFilters] = useState<OrderFilters>({});
    const actionRef = useRef<ActionType>();

    const [currentRecord, setCurrentRecord] = useState<API.OrderVo>();
    const [auditReFundModalVisible, setAuditReFundModalVisible] = useState(false);

    useEffect(() => {
        let canLoad = true;
        listAllApi().then((res) => {
            if (canLoad && res.code === 0 && res.data) {
                setApis(res.data);
            }
        }).finally(() => {
            setLoading(false);
        });
        return () => {
            canLoad = false;
        }
    }, []);

    useEffect(() => {
        actionRef.current?.reload();
    }, [filters]);


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
            width: 250,
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
            title: '实际支付(元)',
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
            title: '数量(次)',
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
            title: '单价(元)',
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
                return "无";
            },
        },
        {
            title: '是否被使用',
            dataIndex: 'isUsed',
            valueType: 'text',
            sorter: false,
            search: false,
            width: 120,
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
            width: 180,
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
                        {canAuditRefund(record.status!) && record!.application &&
                            <Tag
                                style={{ cursor: 'pointer' }}
                                onClick={() => {
                                    setCurrentRecord(record);
                                    setAuditReFundModalVisible(true);
                                }}
                            >
                                退款审核
                            </Tag>
                        }
                    </>
                );
            },
        }
    ];

    return (
        <PageContainer
            title={false}
        >
            <Card>
                <RangeNumberDescription />
                <DateTimeFormatShower />
            </Card>
            <Divider />
            <Select
                style={{ width: 200 }}
                placeholder="选择API"
                value={filters.apiId}
                allowClear
                onChange={(value) => setFilters(prev => ({ ...prev, apiId: value }))}
            >
                {apis.map(api => (
                    <Select.Option key={api.id} value={api.id}>
                        {api.name}
                    </Select.Option>
                ))}
            </Select>
            <ProTable<API.OrderVo, OrderFilters>
                loading={loading}
                pagination={{
                    // 必须有这2个，虽然最终会根据响应的数组长度重新设置，但是pageSize不可以任意
                    pageSize: PAGE_SIZE,
                }}
                headerTitle={'订单列表'}
                actionRef={actionRef}
                rowKey="id"
                cardBordered
                search={{
                    labelWidth: 120,
                }}
                params={filters}
                //@ts-ignore
                request={async (params, sort, filter) => {
                    const current = params.current ? params.current : 1;
                    const size = params.pageSize;
                    delete params.current;
                    delete params.pageSize;
                    try {
                        const data: API.RespPageRespUserVo = await listAllOrder({
                            current,
                            size,
                            sort: mappingSort(sort),
                            //@ts-ignore
                            search: {
                                ...emptyValueToNull(params),
                            }
                        });
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
                columns={columns.map(e => ({
                    ...e,
                    align: "center",
                }))}
            />

            {
                auditReFundModalVisible &&
                <AuditReFundModal
                    userNickname={currentRecord?.userNickname!}
                    username={currentRecord?.username!}
                    application={currentRecord!.application!}
                    open={auditReFundModalVisible}
                    onCancel={() => {
                        setAuditReFundModalVisible(false);
                    }}
                    onOk={async (values) => {
                        const resp = await auditApplication(
                            { id: currentRecord!.application!.id! },
                            {
                                replyContent: values.replyContent,
                                auditStatus: values.auditStatus
                            }
                        );
                        if (resp.code !== 0) {
                            return;
                        }
                        message.success('审核成功');
                        setAuditReFundModalVisible(false);
                        actionRef.current?.reload();
                    }}
                />
            }
        </PageContainer>
    );
};
export default OrderAdminListPage;

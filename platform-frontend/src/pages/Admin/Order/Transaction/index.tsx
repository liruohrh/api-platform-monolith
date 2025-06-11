import { useState } from 'react';
import { Card, Space, Typography } from 'antd';
import { PageContainer, ProColumns, ProTable } from '@ant-design/pro-components';
import { listOrderStatus } from '@/services/api-platform/adminOrderController';
import { mappingDateRange, mappingSort2 } from '@/lib/utils/ant';
import lodash from "lodash";


type Filters = Omit<API.ListOrderStatusReq, "current">
const PAGE_SIZE = 30;

const TransactionStats = () => {
    const [filters, setFilters] = useState<Filters>({});

    const [total, setTotal] = useState({
        amount: 0,
        total: 0
    });


    const columns: ProColumns<API.OrderStatusVo>[] = [
        {
            title: 'API ID',
            dataIndex: 'apiId',
            width: 100,
            search: false,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.apiId}
                </div>
            },
        },
        {
            title: 'API名称',
            dataIndex: 'apiName',
            search: false,
            width: 200,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.apiName}
                </div>
            },
        },
        {
            title: '交易总额(元)',
            dataIndex: 'amount',
            width: 200,
            search: false,
            sorter: filters.dateRange ? true : e => e.amount!,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {`¥${record.amount!.toFixed(5)}`}
                </div>
            },
        },
        {
            title: '订单数量',
            dataIndex: 'total',
            width: 200,
            search: false,
            sorter: filters.dateRange ? true : e => e.total!,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.total}
                </div>
            },
        },
        {
            title: '日期',
            dataIndex: 'date',
            valueType: "dateRange",
            width: 150,
            render(dom, record, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.date}
                </div>
            },
            hidden: !filters.dateRange,
        },
    ];

    return (
        <PageContainer
            title={false}
        >
            <Card>
                <Space direction="vertical" style={{ width: '100%' }}>
                    {/* <Space>
                        <Checkbox
                            checked={isMonth}
                            onChange={(e) => setIsMonth(e.target.checked)}
                        >
                            按月统计
                        </Checkbox>
                    </Space> */}
                    {
                        !filters.dateRange && <Card type="inner">
                            <Space size="large">
                                <Typography.Title level={4} style={{ margin: 0 }}>
                                    总交易额：¥{total.amount.toFixed(5)}
                                </Typography.Title>
                                <Typography.Title level={4} style={{ margin: 0 }}>
                                    总订单数：{total.total}
                                </Typography.Title>
                            </Space>
                        </Card>
                    }

                    <ProTable
                        rowKey={(record) => `${record.date}-${record.apiId}`}
                        pagination={{
                            // 必须有这2个，虽然最终会根据响应的数组长度重新设置，但是pageSize不可以任意
                            pageSize: PAGE_SIZE,
                        }}
                        request={async (params, sort, filter) => {
                            const dateRange = params["date"];
                            if (dateRange) {
                                params["dateRange"] = mappingDateRange(dateRange);
                                setFilters((prev) => ({ ...prev, dateRange: params["dateRange"] }));
                            } else {
                                setFilters((prev) => ({ ...prev, dateRange: undefined }));
                            }
                            const mSort = mappingSort2(sort);
                            const resp = await listOrderStatus({
                                req: {
                                    current: params.current,
                                    dateRange: params["dateRange"],
                                    ...mSort
                                }
                            });
                            const data = resp.data?.data;
                            if (!dateRange && resp.code === 0 && data && data.length > 0) {
                                setTotal({
                                    amount: lodash.sumBy(resp.data!.data, e => e.amount!),
                                    total: lodash.sumBy(resp.data!.data, e => e.total!)
                                });
                            } else {
                                setTotal({ amount: 0, total: 0 });
                            }
                            // if (data && data.length > 0) {
                            //     if (mSort["amountS"] === true) {
                            //         data.sort((a, b) => a.amount! - b.amount!);
                            //     } else if (mSort["amountS"] === false) {
                            //         data.sort((a, b) => b.amount! - a.amount!);
                            //     }

                            //     if (mSort["totalS"] === true) {
                            //         data.sort((a, b) => a.total! - b.total!);
                            //     } else if (mSort["totalS"] === false) {
                            //         data.sort((a, b) => b.total! - a.total!);
                            //     }
                            // }
                            return {
                                data: data,
                                success: resp.code === 0,
                                total: resp.data?.total,
                            };
                        }}
                        toolbar={{
                            settings: [],
                        }}
                        columns={columns.map(e => ({
                            ...e,
                            align: "center",
                        }))}
                    />
                </Space>
            </Card>
        </PageContainer>
    );
};

export default TransactionStats; 
import { useEffect, useState } from 'react';
import { Button, Card, Flex, Input, Select, Space, Table, Typography } from 'antd';
import { apiUsage, apiUsageTimeRange } from '@/services/api-platform/adminStatisticsController';
import { ColumnsType } from 'antd/es/table';
import dayjs from "dayjs";
import { PageContainer } from '@ant-design/pro-components';

type Filters = API.ApiUsageReq
const ApiStats = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState<API.ApiStatus[]>([]);
    const [filters, setFilters] = useState<Filters>({});
    const [currentFilters, setCurrentFilters] = useState<Filters>({});
    const [years, setYears] = useState<number[]>([]);
    const [total, setTotal] = useState({
        callTimes: 0,
        successTimes: 0,
        totalDuration: 0,
    });
    useEffect(() => {
        apiUsageTimeRange().then(res => {
            if (res.code === 0 && res.data) {
                const startYear = dayjs(res.data[0]).year();
                const endYear = dayjs(res.data[1]).year();
                const yearRange = [];
                for (let y = startYear; y <= endYear; y++) {
                    yearRange.push(y);
                }
                setYears(yearRange);
                fetchData();
            }
        });
    }, []);

    useEffect(() => {
        fetchData();
    }, [currentFilters]);

    const fetchData = async () => {
        setLoading(true);
        try {
            const res = await apiUsage({
                req: {
                    ...currentFilters
                }
            });
            if (res.code === 0 && res.data) {
                setData(res.data.status || []);
                setTotal({
                    callTimes: res.data.callTimes!,
                    successTimes: res.data.successTimes!,
                    totalDuration: res.data.totalDuration!,
                });
            } else {
                setData([]);
            }
        } finally {
            setLoading(false);
        }
    };

    const columns: ColumnsType<API.ApiStatus> = [
        {
            title: '日期',
            dataIndex: 'createDate',
            key: 'createDate',
        },
        {
            title: '调用次数',
            dataIndex: 'callTimes',
            key: 'callTimes',
        },
        {
            title: '成功调用次数',
            dataIndex: 'successTimes',
            key: 'successTimes',
        },
        {
            title: '耗时(ms)',
            dataIndex: 'totalDuration',
            key: 'totalDuration',
            // render: (amount: number) => `${amount}`
        }
    ];

    return (
        <PageContainer title={false}>
            <Card>
                <Space direction="vertical" size={"large"} style={{ width: '100%' }}>
                    <Space direction={"horizontal"} size={"large"}>
                        <Space direction={"horizontal"}>
                            <Typography.Text>年份</Typography.Text>
                            <Select
                                style={{ width: 120 }}
                                placeholder="选择年份"
                                allowClear
                                value={filters.year}
                                onChange={(v) => {
                                    setFilters((prev => ({ ...prev, year: v })));
                                }}
                            >
                                {years.map(y => (
                                    <Select.Option key={y} value={y}>{y}年</Select.Option>
                                ))}
                            </Select>
                        </Space>
                        <Space direction={"horizontal"}>
                            <Typography.Text>月份</Typography.Text>
                            <Select
                                style={{ width: 120 }}
                                placeholder="选择月份"
                                allowClear
                                value={filters.month}
                                onChange={(v) => {
                                    setFilters((prev => ({ ...prev, month: v })));
                                }}
                            >
                                {years.length !== 0 && Array.from({ length: 12 }, (_, i) => i + 1).map(m => (
                                    <Select.Option key={m} value={m}>{m}月</Select.Option>
                                ))}
                            </Select>
                        </Space>
                    </Space>
                    <Space direction={"horizontal"}>
                        API名称：
                        <Input
                            style={{ width: 150 }}
                            placeholder={"API名称"}
                            value={filters.apiName}
                            onChange={(e) => {
                                setFilters((prev => ({ ...prev, apiName: e.target.value || undefined })));
                            }}
                        />
                    </Space>
                    <Space direction={"horizontal"}>
                        用户名：
                        <Input
                            style={{ width: 150, marginLeft: 5 }}
                            placeholder={"username"}
                            value={filters.username}
                            onChange={(e) => {
                                setFilters((prev => ({ ...prev, username: e.target.value || undefined })));
                            }}
                        />
                    </Space>

                    <Flex justify="end">
                        <Space>
                            <Button onClick={() => {
                                setFilters({});
                                setCurrentFilters({});
                            }}>重置</Button>
                            <Button type="primary" onClick={() => {
                                setCurrentFilters({ ...filters });
                            }}>查询</Button>
                        </Space>
                    </Flex>

                    {(!data || data.length === 0) ? (
                        <Typography.Title level={4} style={{ textAlign: 'center', margin: '40px 0' }}>
                            {
                                !currentFilters.username && !currentFilters.apiName && "暂无数据"
                            }
                            {
                                !currentFilters.username && currentFilters.apiName && `${currentFilters.apiName} 暂无数据`
                            }
                            {
                                currentFilters.username && !currentFilters.apiName && `${currentFilters.username} 暂无数据`
                            }
                            {
                                currentFilters.username && currentFilters.apiName && `${currentFilters.username} 在 ${currentFilters.apiName} 的使用上暂无数据`
                            }
                        </Typography.Title>
                    ) : (
                        <>
                            <Card type="inner">
                                <Space size="large">
                                    <Typography.Title level={4} style={{ margin: 0 }}>
                                        总调用次数：{total.callTimes}
                                    </Typography.Title>
                                    <Typography.Title level={4} style={{ margin: 0 }}>
                                        总成功调用次数：{total.successTimes}
                                    </Typography.Title>
                                    <Typography.Title level={4} style={{ margin: 0 }}>
                                        总耗时：{total.totalDuration}ms
                                    </Typography.Title>
                                </Space>
                            </Card>
                            <Table
                                columns={columns.map(e => ({
                                    ...e,
                                    align: "center",
                                }))}
                                dataSource={data}
                                rowKey="date"
                                loading={loading}
                                pagination={false}
                            // summary={(pageData) => {
                            //     const totalAmount = pageData.reduce((sum, row) => sum + (row.amount || 0), 0);
                            //     const totalCount = pageData.reduce((sum, row) => sum + (row.total || 0), 0);
                            //     return (
                            //         <Table.Summary.Row>
                            //             <Table.Summary.Cell index={0}>合计</Table.Summary.Cell>
                            //             <Table.Summary.Cell index={1}>¥{totalAmount.toFixed(2)}</Table.Summary.Cell>
                            //             <Table.Summary.Cell index={2}>{totalCount}</Table.Summary.Cell>
                            //         </Table.Summary.Row>
                            //     );
                            // }}
                            />
                        </>
                    )}
                </Space>
            </Card>
        </PageContainer>
    );
};

export default ApiStats;

import { useEffect, useState } from 'react';
import { Card, Select, Space, DatePicker, Input, Typography, notification, Flex, Button } from 'antd';
import { Column } from '@ant-design/charts';
import { apiUsage, apiUsageTimeRange } from '@/services/api-platform/adminStatisticsController';
import { listAllApi } from '@/services/api-platform/apiController';

import dayjs from "dayjs";


const ApiUsageStats = () => {
    const [isLoadingTimeRange, setIsLoadingTimeRange] = useState(true);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState<API.ApiStatus[]>([]);
    const [apis, setApis] = useState<API.HttpApiResp[]>([]);
    const [timeRange, setTimeRange] = useState<number[]>([]);
    const [year, setYear] = useState<number>();
    const [month, setMonth] = useState<number>();
    const [apiId, setApiId] = useState<number>();
    const [userId, setUserId] = useState<number>();
    const [total, setTotal] = useState({
        callTimes: 0,
        successTimes: 0,
        totalDuration: 0
    });

    useEffect(() => {
        apiUsageTimeRange()
            .then(async (v) => {
                if (v.code !== 0) {
                    return;
                }
                setTimeRange(v.data ?? []);
                if (v.data) {
                    listAllApi().then(res => {
                        if (res.code === 0 && res.data) {
                            setApis(res.data);
                        }
                    });
                    fetchData();
                }
            }).finally(() => {
                setIsLoadingTimeRange(false);
            })
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const res = await apiUsage({
                req: {
                    year: Number.isNaN(year) ? undefined : year,
                    month: Number.isNaN(month) ? undefined : month,
                    apiId,
                    userId
                }
            });
            if (res.code === 0 && res.data) {
                setData(res.data.status!);
                setTotal({
                    callTimes: res.data.callTimes || 0,
                    successTimes: res.data.successTimes || 0,
                    totalDuration: res.data.totalDuration || 0
                });
            } else {
                setData([]);
            }
        } finally {
            setLoading(false);
        }
    };



    if (!isLoadingTimeRange && timeRange.length === 0) {
        return (
            <Card title="API Usage">
                <Typography.Title level={1}>
                    暂无API使用数据
                </Typography.Title>
            </Card>
        );
    }

    return (
        <Card title="API Usage" loading={isLoadingTimeRange}>
            <Space direction="vertical" style={{ width: '100%' }}>
                <Space wrap>
                    <DatePicker
                        picker="year"
                        placeholder="选择年份"
                        minDate={dayjs(timeRange[0])}
                        maxDate={dayjs(timeRange[1])}
                        onChange={(date) => setYear(date?.year())}
                    />
                    <DatePicker
                        picker="month"
                        placeholder="选择月份"
                        minDate={dayjs(timeRange[0])}
                        maxDate={dayjs(timeRange[1])}
                        onChange={(date) => setMonth(date?.month() + 1)}
                    />
                    <Select
                        style={{ width: 200 }}
                        placeholder="选择API"
                        allowClear
                        onChange={setApiId}
                    >
                        {apis.map(api => (
                            <Select.Option key={api.id} value={api.id}>
                                {api.name}
                            </Select.Option>
                        ))}
                    </Select>
                    <Input
                        placeholder="用户ID"
                        onChange={(e) => setUserId(Number(e.target.value) || undefined)}
                        style={{ width: 120 }}
                    />
                </Space>
                <Flex justify={"end"}>
                    <Space>
                        <Button
                            onClick={() => {
                                setApiId(undefined);
                                setUserId(undefined);
                                setYear(undefined);
                                setMonth(undefined);
                            }}
                        >清空查询条件</Button>
                        <Button
                            variant="solid"
                            color={"primary"}
                            onClick={() => {
                                fetchData();
                            }}
                        >查询</Button>
                    </Space>
                </Flex>

                {
                    (!data || data.length === 0) && <Typography.Title level={1}>
                        暂无API使用数据
                    </Typography.Title>
                }

                {
                    (data && data.length !== 0) && <>
                        <Card type="inner">
                            <Space size="large">
                                <span>总调用次数：{total.callTimes}</span>
                                <span>总成功次数：{total.successTimes}</span>
                                <span>总耗时：{total.totalDuration}ms</span>
                                <span>平均耗时：{(total.callTimes / total.totalDuration).toFixed(6)}ms</span>
                            </Space>
                        </Card>
                        <Space direction={"vertical"} size={"middle"} style={{ width: "100%" }}>
                            <Typography.Title level={2}>调用次数</Typography.Title>
                            <Column
                                loading={loading}
                                xField='createDate'
                                yField='value'
                                label={{
                                    //@ts-ignore
                                    text: (v) => v.value,
                                    textBaseline: 'bottom',
                                }}
                                data={data.flatMap(item => ({
                                    createDate: item.createDate,
                                    value: item.callTimes,
                                }))}
                            />
                            <Typography.Title level={2}>成功次数</Typography.Title>
                            <Column
                                xField='createDate'
                                yField='value'
                                label={{
                                    //@ts-ignore
                                    text: (v) => v.value,
                                    textBaseline: 'bottom',
                                }}
                                data={data.flatMap(item => ({
                                    createDate: item.createDate,
                                    value: item.successTimes,
                                }))}
                                loading={loading}
                            />
                            <Typography.Title level={2}>耗时</Typography.Title>
                            <Column
                                xField='createDate'
                                yField='value'
                                label={{
                                    //@ts-ignore
                                    text: (v) => `${v.value}ms`,
                                    textBaseline: 'bottom',
                                }}
                                data={data.flatMap(item => ({
                                    createDate: item.createDate,
                                    value: item.totalDuration,
                                }))}
                                loading={loading}
                            />
                        </Space>
                    </>


                }

            </Space>
        </Card >
    );
};

export default ApiUsageStats; 
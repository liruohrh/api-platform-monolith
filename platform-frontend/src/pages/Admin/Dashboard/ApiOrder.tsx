import { useEffect, useState } from 'react';
import { Card, Select, Space, DatePicker, Input } from 'antd';
import { Line } from '@ant-design/charts';
import { apiOrder } from '@/services/api-platform/adminStatisticsController';
import { listAllApi } from '@/services/api-platform/apiController';

const ApiOrderStats = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState<API.ApiOrderStatisticsResp[]>([]);
    const [apis, setApis] = useState<API.HttpApiResp[]>([]);
    const [year, setYear] = useState<number>();
    const [month, setMonth] = useState<number>();
    const [apiId, setApiId] = useState<number>();
    const [userId, setUserId] = useState<number>();
    const [total, setTotal] = useState({
        amount: 0,
        total: 0
    });

    useEffect(() => {
        listAllApi().then(res => {
            if (res.code === 0 && res.data) {
                setApis(res.data);
            }
        });
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const res = await apiOrder({
                req: {
                    year,
                    month,
                    apiId,
                    userId
                }
            });
            if (res.code === 0 && res.data) {
                setData(res.data.status || []);
                setTotal({
                    amount: res.data.amount || 0,
                    total: res.data.total || 0
                });
            }
        } finally {
            setLoading(false);
        }
    };

    const transformedData = data.flatMap(item => ([
        {
            date: item.date,
            value: item.amount,
            type: '订单金额'
        },
        {
            date: item.date,
            value: item.total,
            type: '订单数量'
        }
    ]));

    const config = {
        data: transformedData,
        xField: 'date',
        yField: 'value',
        seriesField: 'type',
        legend: { position: 'top' },
        smooth: true,
        animation: {
            appear: {
                animation: 'path-in',
                duration: 1000,
            },
        },
    };

    return (
        <Card title="API订单统计">
            <Space direction="vertical" style={{ width: '100%' }}>
                <Space wrap>
                    <DatePicker
                        picker="year"
                        placeholder="选择年份"
                        onChange={(date) => setYear(date?.year())}
                    />
                    <DatePicker
                        picker="month"
                        placeholder="选择月份"
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

                <Card type="inner">
                    <Space size="large">
                        <span>总订单金额：¥{total.amount}</span>
                        <span>总订单数：{total.total}</span>
                    </Space>
                </Card>

                <Line {...config} loading={loading} />
            </Space>
        </Card>
    );
};

export default ApiOrderStats; 
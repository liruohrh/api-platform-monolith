import { useEffect, useState } from 'react';
import { Card, Input, Space, Table, Button, Flex, Typography, Modal, message, Checkbox } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { listUserApiUsage, updateUserApiUsage, deleteUserApiUsage } from '@/services/api-platform/adminApiUsageController';
import EditModal from './EditModal';
import { PageContainer } from '@ant-design/pro-components';

type Filters = Omit<API.UserApiUsageReq, "current">;
const PAGE_SIZE = 5;

const UserUsageStats = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState<API.UserApiUsageVo[]>([]);
    const [current, setCurrent] = useState(1);
    const [total, setTotal] = useState(0);
    const [filters, setFilters] = useState<Filters>({});
    const [currentFilters, setCurrentFilters] = useState<Filters>({});

    const [currentRecord, setCurrentRecord] = useState<API.UserApiUsageVo>();
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editLoading, setEditLoading] = useState(false);

    const fetchData = async () => {
        setLoading(true);
        try {
            const res = await listUserApiUsage({
                req: {
                    current,
                    ...currentFilters
                }
            });
            if (res.code === 0 && res.data) {
                setData(res.data.data || []);
                setTotal(res.data.total!);
            } else {
                setData([]);
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [current, currentFilters]);

    const handleEdit = (record: API.UserApiUsageVo) => {
        setCurrentRecord(record);
        setEditModalVisible(true);
    };

    const handleEditSubmit = async (values: API.UserApiUsageUpdateReq) => {
        setEditLoading(true);
        try {
            const res = await updateUserApiUsage(
                {
                    id: currentRecord!.id!,
                },
                values,
            );
            if (res.code === 0) {
                message.success('修改成功');
                setEditModalVisible(false);
                setCurrentRecord(undefined);
                setCurrentFilters({ ...filters });
            }
        } finally {
            setEditLoading(false);
        }
    };

    const handleDelete = (record: API.UserApiUsageVo) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除这条记录吗？',
            onOk: async () => {
                const res = await deleteUserApiUsage({ id: record.id! });
                if (res.code === 0) {
                    message.success('删除成功');
                    setCurrentFilters({ ...filters });
                }
            }
        });
    };

    const columns: ColumnsType<API.UserApiUsageVo> = [
        {
            title: 'id',
            dataIndex: 'id',
        },
        {
            title: 'API名称',
            dataIndex: 'apiName',
            width: 200,
            render(value: string) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '用户名',
            dataIndex: 'username',
            width: 200,
            render(value: string) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '用户昵称',
            dataIndex: 'userNickname',
            width: 200,
            render(value: string) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '剩余次数',
            dataIndex: 'leftTimes',
            sorter: (a, b) => (a.leftTimes || 0) - (b.leftTimes || 0),
            render(v: number, record: API.UserApiUsageVo) {
                if (record.isFreeApi) {
                    return "免费无限";
                } else {
                    return `${v}`;
                }
            },
        },
        {
            title: '免费购买是否用过',
            dataIndex: 'freeUsed',
            key: 'freeUsed',
            width: "50",
            render(v: boolean) {
                if (v) {
                    return "是";
                } else {
                    return "否";
                }
            },
            filterMultiple: false,
            filterOnClose: false,
            filters: [
                {
                    value: true,
                    text: "是"
                },
                {
                    value: false,
                    text: "否"
                }
            ],
            onFilter: (value, record) => {
                return value === record.freeUsed;
            }
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space>
                    <Button type="link" onClick={() => handleEdit(record)}>
                        修改
                    </Button>
                    <Button type="link" danger onClick={() => handleDelete(record)}>
                        删除
                    </Button>
                </Space>
            ),
        }
    ];


    return (
        <PageContainer title={false}>
            <Card>
                <Space direction="vertical" style={{ width: '100%' }}>
                    <Space direction={"vertical"}>
                        <Space direction={"horizontal"}>
                            API名称：
                            <Input
                                style={{ width: 150 }}
                                placeholder={"API名称"}
                                value={filters.apiName}
                                onChange={(e) => setFilters((prev) => ({ ...prev, apiName: e.target.value || undefined }))}
                            />
                        </Space>
                        <Space direction={"horizontal"}>
                            用户名：
                            <Input
                                style={{ width: 150, marginLeft: 5 }}
                                placeholder={"username"}
                                value={filters.username}
                                onChange={(e) => setFilters((prev) => ({ ...prev, username: e.target.value || undefined }))}
                            />
                        </Space>
                        <Space direction={"horizontal"}>
                            排除免费API：
                            <Checkbox
                                checked={filters.excludeFreeApi}
                                onChange={(e) => setFilters((prev) => ({ ...prev, excludeFreeApi: e.target.checked }))}
                            />
                        </Space>
                    </Space>

                    <Flex justify="end">
                        <Space>
                            <Button onClick={() => {
                                setFilters({});
                            }}>重置</Button>
                            <Button
                                variant={"solid"}
                                color={"primary"}
                                onClick={() => {
                                    setCurrent(1);
                                    setCurrentFilters({ ...filters });
                                }}
                            >查询</Button>
                        </Space>
                    </Flex>

                    {(!data || data.length === 0) ? (
                        <Typography.Title level={4} style={{ textAlign: 'center', margin: '40px 0' }}>
                            暂无数据
                        </Typography.Title>
                    ) : (
                        <Table
                            loading={loading}
                            columns={columns.map(e => ({
                                ...e,
                                align: "center",
                            }))}
                            dataSource={data}
                            rowKey={"id"}
                            pagination={{
                                current: current,
                                pageSize: 5,
                                total,
                                onChange: (page, size) => {
                                    setCurrent(page);
                                }
                            }}
                        />
                    )}
                </Space>

                <EditModal
                    open={editModalVisible}
                    onCancel={() => {
                        setEditModalVisible(false);
                        setCurrentRecord(undefined);
                    }}
                    onOk={handleEditSubmit}
                    initialValues={currentRecord}
                    loading={editLoading}
                />
            </Card>
        </PageContainer>
    );
};

export default UserUsageStats;

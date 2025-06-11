import { useEffect, useState } from 'react';
import { Card, Table, Space, Button, Checkbox, Select, Modal, Form, Input, message, Flex, Typography, Tag, Tooltip, Dropdown } from 'antd';
import { listAllComment } from '@/services/api-platform/adminCommentController';
import { listAllApi } from '@/services/api-platform/apiController';
import { formatTimestamp } from '@/utils';
import { replyComment } from '@/services/api-platform/userCommentController';
import { ColumnsType } from 'antd/lib/table';
import { DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import { deleteComment } from '@/services/api-platform/commentController';
import ReadModal from './components/ReadModal';
import { Link } from '@umijs/max';
import { PageContainer } from '@ant-design/pro-components';

const PAGE_SIZE = 5;

type Filters = Omit<API.listAllCommentParams, "current">
const CommentManagement = () => {
    const [loading, setLoading] = useState(false);
    const [apis, setApis] = useState<API.HttpApiResp[]>([]);
    const [comments, setComments] = useState<API.CommentPageVo[]>([]);
    const [total, setTotal] = useState(0);
    const [current, setCurrent] = useState(1);
    // 过滤条件状态
    const [filters, setFilters] = useState<Filters>({});
    const [currentFilters, setCurrentFilters] = useState<Filters>({});

    const [currentFilterComment, setCurrentFilterComment] = useState<API.CommentPageVo | undefined>(undefined);

    const [currentRecord, setCurrentRecord] = useState<API.CommentPageVo | undefined>();

    const [readModalVisible, setReadModalVisible] = useState(false);

    // 回复Modal状态
    const [replyModalVisible, setReplyModalVisible] = useState(false);
    const [form] = Form.useForm();


    // 获取所有API列表
    useEffect(() => {
        let canLoad = true;
        listAllApi().then((res) => {
            if (canLoad && res.code === 0 && res.data) {
                setApis(res.data);
            }
        });
        fetchData();
        return () => {
            canLoad = false;
        }
    }, []);

    useEffect(() => {
        fetchData();
    }, [current, currentFilters]);

    // 获取评论列表
    const fetchData = async () => {
        setLoading(true);
        try {
            const res = await listAllComment({
                ...currentFilters,
                current: current,
            });
            if (res.data) {
                setComments(res.data.data || []);
                setTotal(res.data.total || 0);
            }
        } finally {
            setLoading(false);
        }
    };

    const columns: ColumnsType<API.CommentPageVo> = [
        {
            title: 'id',
            dataIndex: 'id',
            width: 50,
        },
        // {
        //     title: 'API名称',
        //     dataIndex: 'apiName',
        //     key: 'apiName',
        //     render: (_: any, record: API.CommentPageVo) => {
        //         return (
        //             <Link to={`/api/info/${record.apiId}`}>{record.apiName}</Link>
        //         )
        //     }
        // },
        {
            title: '用户名',
            dataIndex: 'username',
            width: 150,
            render(value: string) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '用户昵称',
            dataIndex: 'userNickname',
            width: 150,
            render(value: string) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '评论内容',
            dataIndex: 'content',
            width: 300,
            render(value: string) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '评论时间',
            dataIndex: 'ctime',
            width: 160,
            render: (ctime: number) => formatTimestamp(ctime),
        },
        // {
        //     title: '评分',
        //     dataIndex: 'score',
        //     key: 'score',
        //     render: (_: any, record: API.CommentPageVo) => {
        //         if (record.isRoot) {
        //             return record.score;
        //         } else {
        //             return "回复无"
        //         }
        //     },
        //     hidden: currentFilters.replyToMe || (currentFilters.username !== undefined && currentFilters.isUserReply)
        // },
        // {
        //     title: '点赞数',
        //     dataIndex: 'favorCount',
        //     key: 'favorCount',
        // },
        // {
        //     title: '回复数',
        //     dataIndex: 'replyCount',
        //     key: 'replyCount',
        // },
        // {
        //     title: '评论类型',
        //     dataIndex: 'isRoot',
        //     key: 'isRoot',
        //     render: (isRoot: boolean) => (
        //         <span>{isRoot ? '评论' : '回复'}</span>
        //     ),
        //     hidden: currentFilters.replyToMe
        // },
        {
            title: "我有无回复",
            dataIndex: 'adminReplies',
            width: 110,
            render: (adminReplies?: API.CommentVo[]) => {
                return adminReplies ? `有` : "无";
            },
        },
        {
            title: '操作',
            key: 'action',
            width: 150,
            render: (_: any, record: API.CommentPageVo) => (
                <Space direction={"horizontal"} size={8}>
                    <Button
                        style={{
                            padding: 0
                        }}
                        type={"link"}
                        color={"primary"}
                        onClick={() => {
                            setCurrentRecord(record);
                            setReadModalVisible(true);
                        }}>
                        查看
                    </Button>
                    <Button
                        style={{
                            padding: 0
                        }}
                        type={"link"}
                        color={"primary"}
                        onClick={() => {
                            setCurrentRecord(record);
                            setReplyModalVisible(true);
                        }}>
                        回复
                    </Button>
                    {/* {
                        record.isRoot && <Button
                            style={{
                                padding: 0
                            }}
                            disabled={record.replyCount === 0}
                            type={"link"}
                            color={"primary"}
                            onClick={() => {
                                const filters0 = { ...filters };
                                filters0.username = undefined;
                                filters0.isUserReply = undefined;
                                filters0.isRoot = undefined;
                                filters0.replyToCommentId = undefined;
                                filters0.rootCommentId = record.id;
                                setCurrentFilterComment(record);
                                setFilters(filters0);
                                setCurrent(1);
                                setCurrentFilters(filters0);
                            }}>
                            所有评论
                        </Button>
                    } */}
                    {
                        record.replyCount !== 0 &&
                        <Button
                            style={{
                                padding: 0
                            }}
                            type={"link"}
                            color={"primary"}
                            onClick={() => {
                                const filters0 = { ...filters };
                                filters0.username = undefined;
                                filters0.isUserReply = undefined;
                                filters0.isRoot = undefined;
                                filters0.replyToCommentId = record.id;
                                filters0.rootCommentId = undefined;
                                setCurrentFilterComment(record);
                                setFilters(filters0);
                                setCurrent(1);
                                setCurrentFilters(filters0);
                            }}>
                            所有回复信息
                        </Button>
                    }
                    <Button
                        style={{
                            padding: 0
                        }}
                        type={"link"}
                        color={"primary"}
                        onClick={async () => {
                            const resp = await deleteComment({
                                id: record.id!
                            });
                            if (resp.code !== 0) {
                                return;
                            }
                            setComments([
                                ...comments.filter(e => e.id !== record.id)
                            ]);
                        }}>
                        <div>
                            删除<DeleteOutlined style={{ color: "red" }} />
                        </div>
                    </Button>
                </Space>
            ),
        }];

    const handleReplySubmit = async () => {
        const values = await form.validateFields();
        if (!currentRecord) {
            return;
        }

        const resp = await replyComment({
            apiId: currentRecord.apiId,
            replyToCommentId: currentRecord.id,
            content: values.content
        });
        if (resp.code !== 0) {
            return;
        }

        message.success('回复成功');
        setReplyModalVisible(false);
        form.resetFields();
        setCurrentFilters({ ...filters });
    };

    return (
        <PageContainer
            title={false}
        >
            <Card>
                <Space direction="vertical" style={{ width: '100%', marginBottom: 16 }}>
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
                    <Checkbox
                        checked={filters.isRoot}
                        onChange={(e) => {
                            setCurrentFilterComment(undefined);
                            setFilters(prev => ({
                                ...prev,
                                rootCommentId: undefined,
                                replyToCommentId: undefined,
                                replyToMe: undefined,
                                excludeMe: undefined,
                                isRoot: e.target.checked
                            }));
                        }}
                    >
                        仅根评论
                    </Checkbox>
                    <Space direction={"horizontal"}>
                        <Checkbox
                            checked={filters.replyToMe}
                            onChange={(e) => setFilters(prev => ({ ...prev, replyToMe: e.target.checked }))}
                        >
                            回复给我的
                        </Checkbox>
                        <Checkbox
                            checked={filters.excludeMe}
                            onChange={(e) => setFilters(prev => ({ ...prev, excludeMe: e.target.checked }))}
                        >
                            排除我的
                        </Checkbox>
                    </Space>
                    <Space>
                        用户名：
                        <Input
                            value={filters.username}
                            onChange={(e) => setFilters(prev => ({ ...prev, username: e.target.value ? e.target.value : undefined }))}
                        />（默认查询用户评论）
                        <Checkbox
                            checked={filters.isUserReply}
                            onChange={(e) => setFilters(prev => ({ ...prev, isUserReply: e.target.checked }))}
                        >
                            回复用户的（联合用户名查询）
                        </Checkbox>
                    </Space>
                    <Space>
                        排序：
                        <Select
                            value={filters.ctime}
                            style={{ width: 120 }}
                            placeholder="创建时间"
                            allowClear
                            onChange={(value) => setFilters(prev => ({ ...prev, ctime: value }))}
                        >
                            <Select.Option value={true}>升序</Select.Option>
                            <Select.Option value={false}>降序</Select.Option>
                        </Select>
                        <Select
                            value={filters.favorCount}
                            style={{ width: 120 }}
                            placeholder="点赞数"
                            allowClear
                            onChange={(value) => setFilters(prev => ({ ...prev, favorCount: value }))}
                        >
                            <Select.Option value={true}>升序</Select.Option>
                            <Select.Option value={false}>降序</Select.Option>
                        </Select>
                        <Select
                            value={filters.replyCount}
                            style={{ width: 120 }}
                            placeholder="回复数"
                            allowClear
                            onChange={(value) => setFilters(prev => ({ ...prev, replyCount: value }))}
                        >
                            <Select.Option value={true}>升序</Select.Option>
                            <Select.Option value={false}>降序</Select.Option>
                        </Select>
                    </Space>
                    <Flex justify='end'>
                        <Space>
                            <Button
                                onClick={() => {
                                    setCurrentFilters({ ...filters });
                                }}
                            >
                                <ReloadOutlined />
                            </Button>
                            <Button
                                onClick={() => {
                                    setCurrentFilterComment(undefined);
                                    setFilters((prev) => ({
                                        rootCommentId: undefined,
                                        replyToCommentId: undefined
                                    }));
                                }}
                            >清除当前评论</Button>
                            <Button
                                onClick={() => {
                                    setFilters((prev) => ({
                                        rootCommentId: prev.rootCommentId,
                                        replyToCommentId: prev.replyToCommentId
                                    }));
                                }}
                            >清空查询条件</Button>
                            <Button
                                variant="solid"
                                color={"primary"}
                                onClick={() => {
                                    setCurrent(1);
                                    setCurrentFilters({ ...filters });
                                }}
                            >查询</Button>
                        </Space>
                    </Flex>
                </Space>
                {
                    currentFilterComment &&
                    <Space direction={"vertical"} style={{ color: "red" }}>
                        {
                            currentFilters.rootCommentId && <div>
                                当前是 id= {currentFilters.rootCommentId} 的所有评论
                            </div>
                        }
                        {
                            currentFilters.replyToCommentId && <div>
                                当前是 id= {currentFilters.replyToCommentId} 的所有回复
                            </div>
                        }

                        <Flex align={"end"}>
                            <Card style={{ marginLeft: 20, marginBottom: 10 }}>

                                <Space direction={"vertical"} >
                                    <Typography.Paragraph type="secondary">
                                        @{currentFilterComment.userNickname}：{currentFilterComment.content}
                                    </Typography.Paragraph>
                                    <Space>
                                        <Link to={`/api/info/${currentFilterComment.apiId}`}><Tag color='cyan'>{currentFilterComment.apiName}</Tag></Link>
                                        <Space direction={"horizontal"} size={"large"}>
                                            <Space>
                                                <span>👍 {currentFilterComment.favorCount || 0}</span>
                                                <span>💬 {currentFilterComment.replyCount || 0}</span>
                                            </Space>
                                        </Space>
                                        <Typography.Text type="secondary">
                                            {formatTimestamp(currentFilterComment.ctime)}
                                        </Typography.Text>
                                    </Space>
                                </Space>
                            </Card>
                            <div style={{ marginLeft: 20, marginBottom: 10 }}>
                                {/* {
                                    !currentFilters.rootCommentId && <Button
                                        disabled={currentFilterComment.replyCount === 0 || !currentFilterComment.isRoot}
                                        color={"primary"}
                                        onClick={() => {
                                            const filters0 = { ...filters };
                                            filters0.username = undefined;
                                            filters0.isUserReply = undefined;
                                            filters0.isRoot = undefined;
                                            filters0.replyToCommentId = undefined;
                                            filters0.rootCommentId = currentFilterComment.id;
                                            setFilters(filters0);
                                            setCurrent(1);
                                            setCurrentFilters(filters0);
                                        }}>
                                        所有评论
                                    </Button>
                                } */}
                                {
                                    !currentFilters.replyToCommentId && <Button
                                        disabled={currentFilterComment.replyCount === 0}
                                        color={"primary"}
                                        onClick={() => {
                                            const filters0 = { ...filters };
                                            filters0.username = undefined;
                                            filters0.isUserReply = undefined;
                                            filters0.isRoot = undefined;
                                            filters0.replyToCommentId = currentFilterComment.id;
                                            filters0.rootCommentId = undefined;
                                            setFilters(filters0);
                                            setCurrent(1);
                                            setCurrentFilters(filters0);
                                        }}>
                                        所有回复信息
                                    </Button>
                                }
                            </div>
                        </Flex>
                    </Space>
                }
                <Table
                    columns={columns.map(e => ({
                        ...e,
                        align: "center",
                    }))}
                    dataSource={comments}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                        current,
                        pageSize: PAGE_SIZE,
                        total,
                        onChange: (page, size) => {
                            setCurrent(page);
                        },
                    }}
                />
                {
                    readModalVisible && <ReadModal
                        open={readModalVisible}
                        record={currentRecord!}
                        onCancel={() => {
                            setReadModalVisible(false);
                        }}
                    />
                }

                {
                    replyModalVisible && <Modal
                        title="回复评论"
                        open={replyModalVisible}
                        onOk={handleReplySubmit}
                        onCancel={() => {
                            setReplyModalVisible(false);
                            form.resetFields();
                        }}
                    >
                        <Form form={form}>
                            <Form.Item
                                name="content"
                                rules={[{ required: true, message: '请输入回复内容' }]}
                            >
                                <Input.TextArea rows={4} placeholder="请输入回复内容" />
                            </Form.Item>
                        </Form>
                    </Modal>
                }
            </Card>
        </PageContainer>
    );
};

export default CommentManagement; 
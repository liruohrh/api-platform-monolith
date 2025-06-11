import { useRef, useState } from 'react';
import { Card, Space, Button, Modal, Form, Input, message } from 'antd';
import { listCommentReportApplication, auditApplication } from '@/services/api-platform/applicationController';
import { formatTimestamp } from '@/utils';
import { ActionType, PageContainer, ProColumns, ProTable } from '@ant-design/pro-components';
import ReadModal from './components/ReadModal';
import StatusTag from './components/StatusTag';
import { REPORT_REASONS } from '@/lib/utils/application';
import { mappingRange, mappingSort2 } from '@/lib/utils/ant';

const PAGE_SIZE = 5;

const Report = () => {
    const [loading, setLoading] = useState(false);
    const [auditModalVisible, setAuditModalVisible] = useState(false);


    const [currentRecord, setCurrentRecord] = useState<API.CommentReportApplicationVo | undefined>(undefined);

    const [readModalVisible, setReadModalVisible] = useState(false);

    const [form] = Form.useForm();
    const actionRef = useRef<ActionType | undefined>();


    const handleAudit = (report: API.CommentReportApplicationVo) => {
        setCurrentRecord(report);
        setAuditModalVisible(true);
    };

    const handleAuditSubmit = async (
        status: 'PENDING_AUDIT' | 'APPROVED' | 'NOT_PASS',
        report?: API.CommentReportApplicationVo
    ) => {
        if (!report && !currentRecord) return;
        if (!report) {
            report = currentRecord;
        }
        const values = await form.validateFields();
        const resp = await auditApplication(
            { id: report!.id! },
            {
                replyContent: values.replyContent,
                auditStatus: status
            }
        );
        if (resp.code !== 0) {
            return;
        }
        actionRef.current?.reload();
        setCurrentRecord(undefined);
        message.success('审核成功');
        setAuditModalVisible(false);
    };

    const columns: ProColumns<API.CommentReportApplicationVo>[] = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            sorter: false,
            search: false
        },
        {
            title: '举报者',
            dataIndex: 'reporterNickname',
            width: 200,
            sorter: false,
            search: true,
            render(_, record) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {record.reporterNickname}@{record.reporterUsername}
                </div>
            },
        },
        {
            title: '举报原因',
            dataIndex: 'reason',
            width: 150,
            search: true,
            sorter: false,
            valueEnum: Object.assign({}, ...REPORT_REASONS.map(e => ({
                [e.value]: {
                    text: e.label
                }
            }))),
            render(_, record) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {REPORT_REASONS.find(e => e.value === record.reason)?.label ?? record.reason}
                </div>
            },
        },
        {
            title: '详细说明',
            dataIndex: 'description',
            width: 450,
            sorter: false,
            search: false,
            render(_, record) {
                let value = record.description;
                if (!value) {
                    value = REPORT_REASONS.find(e => e.value === record.reason)?.description ?? "";
                }
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {value}
                </div>
            },
        },
        {
            title: '审核状态',
            dataIndex: 'auditStatus',
            width: 80,
            search: true,
            valueEnum: {
                0: {
                    text: "待审核"
                },
                1: {
                    text: "已通过"
                },
                2: {
                    text: "未通过"
                },
            },
            render: (_, record) => <StatusTag status={record.auditStatus!} />,
        },
        // {
        //     title: '被举报者',
        //     dataIndex: 'reportedNickName',
        //     key: 'reportedNickName',
        //     render: (_: any, record: API.CommentReportApplicationVo) => {
        //         return record.reportedComment!.userNickName;
        //     },
        // },
        // {
        //     title: <span>
        //         被举报的评论内容<InfoCircleOutlined />
        //     </span>,
        //     dataIndex: 'reportedNickName',
        //     key: 'reportedNickName',
        //     render: (_: any, record: API.CommentReportApplicationVo) => {
        //         return (
        //             <Tooltip
        //                 title={
        //                     <Space direction={"vertical"} style={{ color: "black" }}>
        //                         <Space>
        //                             <span>👍 {record.reportedComment!.favorCount || 0}</span>
        //                             <span>💬 {record.reportedComment!.replyCount || 0}</span>
        //                         </Space>
        //                         <Typography.Paragraph>
        //                             {formatTimestamp(record.reportedComment!.ctime)}
        //                         </Typography.Paragraph>
        //                     </Space>
        //                 }
        //                 color={"gold"}
        //             >
        //                 {record.reportedComment!.content}
        //             </Tooltip>
        //         )
        //     },
        // },
        // {
        //     title: '审核回复',
        //     dataIndex: 'replyContent',
        //     key: 'replyContent',
        //     render: (replyContent?: string) => {
        //         return replyContent ? replyContent : "未回复";
        //     },
        // },
        {
            title: '举报时间',
            dataIndex: 'ctime',
            width: 160,
            valueType: "dateTimeRange",
            sorter: true,
            search: true,
            render: (_, record) => formatTimestamp(record.ctime!),
        },
        {
            title: '操作',
            sorter: false,
            search: false,
            width: 150,
            render: (_, record) => (
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
                        style={{ padding: 0 }}
                        type="link" onClick={() => handleAudit(record)}>
                        审核
                    </Button>
                </Space>
            ),
        },
    ];

    return (
        <PageContainer
            title={false}
        >
            <Card>
                <ProTable
                    actionRef={actionRef}
                    columns={columns.map(e => ({
                        ...e,
                        align: "center",
                    }))}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                        pageSize: PAGE_SIZE,
                    }}
                    request={async (params, sort, filter) => {
                        const mparams = mappingRange(params);
                        mparams["reporterName"] = mparams["reporterNickname"];
                        delete mparams["reporterNickname"];
                        const msort = mappingSort2(sort);
                        const res = await listCommentReportApplication({
                            req: {
                                ...mparams,
                                ...msort,
                                current: params.current!,
                            }
                        });
                        return {
                            success: res.code === 0,
                            total: res.data?.total,
                            data: res.data?.data
                        }
                    }}
                />

                {
                    readModalVisible && <ReadModal
                        open={readModalVisible}
                        record={currentRecord!}
                        onCancel={() => setReadModalVisible(false)}
                    />
                }
                {
                    auditModalVisible && <Modal
                        title="审核举报"
                        open={auditModalVisible}
                        footer={[
                            <Button key="cancel" onClick={() => setAuditModalVisible(false)}>
                                取消
                            </Button>,
                            <Button
                                key="reject"
                                danger
                                onClick={() => handleAuditSubmit('NOT_PASS')}
                            >
                                拒绝
                            </Button>,
                            <Button
                                key="approve"
                                type="primary"
                                onClick={() => handleAuditSubmit('APPROVED')}
                            >
                                通过
                            </Button>,
                        ]}
                        destroyOnClose
                        onCancel={() => {
                            setCurrentRecord(undefined);
                            setAuditModalVisible(false);
                            form.resetFields();
                        }}
                    >
                        <Form form={form}>
                            <Form.Item
                                name="replyContent"
                                label="审核回复"
                                rules={[{ required: false, message: '请输入审核回复' }]}
                            >
                                <Input.TextArea defaultValue={currentRecord?.replyContent} rows={4} placeholder="请输入审核回复" />
                            </Form.Item>
                        </Form>
                    </Modal>

                }

            </Card>
        </PageContainer >
    );
};

export default Report; 
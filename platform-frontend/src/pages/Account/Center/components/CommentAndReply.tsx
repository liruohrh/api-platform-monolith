import { commentScore } from '@/lib/utils/math';
import { listUserComment } from '@/services/api-platform/userCommentController';
import { formatTimestamp } from '@/utils';
import { ProList } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Card, Flex, Rate, Space, Tag, Typography } from 'antd';
import { useState } from 'react';

export default function CommentAndReply() {
    const [loading, setLoading] = useState(false);

    return (
        <ProList<API.UserCommentVo>
            loading={loading}
            pagination={{
                defaultPageSize: 20,
                showSizeChanger: true,
            }}
            request={async (params) => {
                setLoading(true);
                try {
                    const resp = await listUserComment({
                        commonPageReq: {
                            current: params.current || 1,
                            size: params.pageSize,
                        },
                    });
                    return {
                        data: resp.data?.data || [],
                        success: resp.code === 0,
                        total: resp.data?.total || 0,
                    };
                } finally {
                    setLoading(false);
                }
            }}
            metas={{
                title: {
                    render: (_, record) => (
                        <Link to={`/api/info/${record.apiId}`}>
                            <Typography.Title level={5}>
                                {record.apiName}
                            </Typography.Title>
                        </Link>
                    ),
                },
                description: {
                    render: (_, record) => (
                        <Space direction="vertical" size="small" style={{ width: "100%" }}>
                            <Space>
                                {record.isReply ? (
                                    <Tag color="blue">回复</Tag>
                                ) : (
                                    <Tag color="green">评论</Tag>
                                )}
                                {!record.isReply && (
                                    <Rate disabled defaultValue={commentScore(record.score!)} />
                                )}
                            </Space>
                            <Typography.Paragraph>{record.content}</Typography.Paragraph>
                            <Flex justify={"space-between"}>
                                <Space direction={"horizontal"} size={"large"}>
                                    <Space>
                                        <span>👍 {record.favorCount || 0}</span>
                                        <span>💬 {record.replyCount || 0}</span>
                                    </Space>
                                </Space>
                                <Typography.Text type="secondary">
                                    {formatTimestamp(record.ctime)}
                                </Typography.Text>
                            </Flex>

                            {record.isReply && record.addresseeComment && record.addresseeNickname && (
                                <Space direction={"vertical"} size={"small"} style={{ width: "100%" }}>
                                    <Card style={{ width: "100%" }}>
                                        <Typography.Paragraph type="secondary">
                                            @{record.addresseeNickname}：{record.addresseeComment!.content}
                                        </Typography.Paragraph>
                                        <Flex justify={"space-between"}>
                                            <Space direction={"horizontal"} size={"large"}>
                                                <span>👍 {record.addresseeComment!.favorCount || 0}</span>
                                                <span>💬 {record.addresseeComment!.replyCount || 0}</span>
                                            </Space>
                                            <Typography.Text type="secondary">
                                                {formatTimestamp(record.addresseeComment!.ctime)}
                                            </Typography.Text>
                                        </Flex>
                                    </Card>
                                </Space>
                            )}
                            {
                                record.isReply && !record.addresseeComment && record.addresseeNickname &&
                                <Typography.Paragraph type={"warning"}>@{record.addresseeNickname}的该评论被删除</Typography.Paragraph>
                            }
                        </Space>
                    ),
                },
            }}
        />
    );
}

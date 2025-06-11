import { getUnReadReplyCount, listReplyToUserComment, readReply, replyComment } from '@/services/api-platform/userCommentController';
import { favorComment } from '@/services/api-platform/commentController';
import { formatTimestamp } from '@/utils';
import { ActionType, ProList } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Badge, Button, Card, Flex, Input, Space, Spin, Typography, notification } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { CommentOutlined, LikeOutlined } from '@ant-design/icons';

const ReplyItem: React.FC<{
    item: API.CommentReplyVo;
    onRead: (id: number) => Promise<boolean>;
}> = ({ item, onRead }) => {
    const [isCommenting, setIsCommenting] = useState(false);
    const [commentContent, setCommentContent] = useState('');
    const [isSubmittingComment, setIsSubmittingComment] = useState(false);
    const [isFavoring, setIsFavoring] = useState(false);
    const [isFavor, setIsFavor] = useState(item.isFavor!);
    const [favorCount, setFavorCount] = useState(item.favorCount!);
    const [replyCount, setReplyCount] = useState(item.replyCount!);
    const [isRead, setIsRead] = useState(item.isRead);
    useEffect(() => {
        setIsRead(item.isRead);
    }, [item.isRead]);
    useEffect(() => {
        setIsFavor(item.isFavor!);
    }, [item.isFavor]);
    // 处理点赞
    const handleFavor = async () => {
        setIsFavoring(true);
        try {
            const resp = await favorComment({ id: item.id! });
            if (resp.code === 0 && resp.data) {
                setIsFavor(!isFavor);
                setFavorCount(!isFavor ? favorCount + 1 : favorCount - 1);
            }
        } finally {
            setIsFavoring(false);
        }
    };

    // 处理回复提交
    const handleReplySubmit = async () => {
        if (!commentContent.trim()) {
            notification.warning({ message: '请输入回复内容' });
            return;
        }
        setIsSubmittingComment(true);
        try {
            const resp = await replyComment({
                apiId: item.apiId!,
                replyToCommentId: item.id,
                content: commentContent
            });
            if (resp.code === 0) {
                setIsCommenting(false);
                setReplyCount(replyCount + 1);
                setCommentContent('');
                notification.success({ message: '回复成功' });
            }
        } finally {
            setIsSubmittingComment(false);
        }
    };

    return (
        <Card
            onClick={() => {
                if (!isRead) {
                    onRead(item.id!)
                        .then(v => {
                            setIsRead(true);
                        });
                }
            }}
        >
            <Space direction="vertical" size="small" style={{ width: '100%' }}>
                <Space>
                    <Link to={`/api/info/${item.apiId}`}>
                        <Typography.Title level={5}>
                            {item.apiName}
                        </Typography.Title>
                    </Link>
                    {!isRead && (
                        <Badge status="processing" text="未读" />
                    )}
                </Space>

                <Typography.Text>
                    <Typography.Text strong>
                        @{item.userNickname}
                    </Typography.Text>
                    {' 回复了你'}
                </Typography.Text>

                <Typography.Paragraph
                    style={{
                        background: '#f5f5f5',
                        padding: '8px 12px',
                        borderRadius: '4px',
                        cursor: 'pointer'
                    }}
                >
                    {item.content}
                </Typography.Paragraph>

                <Flex justify="space-between">
                    <Space>
                        {isFavoring ? (
                            <Spin size="small" />
                        ) : (
                            <span
                                style={{ cursor: 'pointer' }}
                                onClick={handleFavor}
                            >
                                <LikeOutlined style={{ color: isFavor ? "skyblue" : "" }} />
                                <Typography.Text style={{ color: isFavor ? "skyblue" : "" }}>
                                    {favorCount === 0 ? "点赞" : favorCount}
                                </Typography.Text>
                            </span>
                        )}

                        <span
                            style={{ cursor: 'pointer' }}
                            onClick={() => {
                                setIsCommenting(!isCommenting);
                            }}
                        >
                            <CommentOutlined style={{ color: isCommenting ? "skyblue" : "" }} />
                            <Typography.Text style={{ color: isCommenting ? "skyblue" : "" }}>
                                {!isCommenting ? (replyCount === 0 ? "回复" : replyCount) : "取消回复"}
                            </Typography.Text>
                        </span>
                    </Space>
                    <Typography.Text type="secondary">
                        {formatTimestamp(item.ctime)}
                    </Typography.Text>
                </Flex>

                {isCommenting && (
                    <div>
                        <Input.TextArea
                            showCount
                            maxLength={500}
                            value={commentContent}
                            onChange={(e) => setCommentContent(e.target.value)}
                            placeholder="写下你的回复..."
                        />
                        <Flex justify="end" style={{ marginTop: 16 }}>
                            {isSubmittingComment ? (
                                <Spin />
                            ) : (
                                <Button type="primary" onClick={handleReplySubmit}>
                                    发送回复
                                </Button>
                            )}
                        </Flex>
                    </div>
                )}

                <Card>
                    {
                        item.userComment && <>
                            <Typography.Paragraph type="secondary">
                                我：{item.userComment!.content}
                            </Typography.Paragraph>
                            <Flex justify={"space-between"}>
                                <Space>
                                    <span>👍 {item.userComment!.favorCount || 0}</span>
                                    <span>💬 {item.userComment!.replyCount || 0}</span>
                                </Space>
                                <Typography.Text type="secondary">
                                    {formatTimestamp(item.userComment!.ctime)}
                                </Typography.Text>
                            </Flex>
                        </>
                    }
                    {
                        !item.userComment && <>
                            <Typography.Paragraph type={"warning"}>
                                我的这个评论被删除了
                            </Typography.Paragraph>
                        </>
                    }
                </Card>
            </Space>
        </Card>
    );

}

export default function ReplyToMe() {
    const [loading, setLoading] = useState(true);
    const actionRef = useRef<ActionType | undefined>();

    const [unreadReplyCount, setUnreadReplyCount] = useState(0);
    useEffect(() => {
        let canLoad = true;
        getUnReadReplyCount()
            .then((resp) => {
                if (resp.code !== 0 || !canLoad) {
                    return;
                }
                setLoading(false);
                setUnreadReplyCount(resp.data!);
            });

        return () => {
            canLoad = false;
        }
    }, []);

    if (loading) {
        return <Spin />;
    }

    // 标记已读
    const handleRead = async (commentId?: number): Promise<boolean> => {
        try {
            const resp = await readReply({ commentId });
            if (resp.code === 0) {
                if (!commentId) {
                    setUnreadReplyCount(0);
                    actionRef.current?.reload();
                } else {
                    setUnreadReplyCount(unreadReplyCount - 1);
                }
            }
            return true;
        } catch (error) {
            notification.error({
                message: '标记已读失败',
            });
            return false;
        }
    };

    // 一键已读
    const handleReadAll = async () => {
        if (unreadReplyCount === 0) {
            return;
        }
        await handleRead();
        notification.success({
            message: '全部标记为已读'
        });
    };

    return (
        <Card
            title={`回复我的${unreadReplyCount ? `(${unreadReplyCount})` : ""}`}
            extra={
                <Button type="primary" onClick={handleReadAll}>
                    一键已读
                </Button>
            }
        >
            <ProList<API.CommentReplyVo>
                actionRef={actionRef}
                pagination={{
                    defaultPageSize: 20,
                    showSizeChanger: true,
                }}
                request={async (params) => {
                    const resp = await listReplyToUserComment({
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

                }}
                renderItem={(item) => {
                    return (
                        <ReplyItem
                            key={item.id}
                            item={item}
                            onRead={handleRead}
                        />
                    );
                }}
            />
        </Card>
    );
}

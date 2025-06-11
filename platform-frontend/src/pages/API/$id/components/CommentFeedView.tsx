import { loginPath } from '@/constants';
import { REPORT_REASONS } from '@/lib/utils/application';
import { commentScore } from '@/lib/utils/math';
import { reportComment } from '@/services/api-platform/applicationController';
import { deleteComment, favorComment, listComment, postComment } from '@/services/api-platform/commentController';
import { replyComment } from '@/services/api-platform/userCommentController';
import { formatTimestamp } from '@/utils'
import { ArrowDownOutlined, CommentOutlined, DeleteColumnOutlined, DeleteOutlined, LikeOutlined, MoreOutlined, WarningOutlined } from '@ant-design/icons';
import { useModel, history } from '@umijs/max';
import { Button, Card, Dropdown, Flex, Form, Input, Modal, notification, Radio, Space, Spin, Typography } from 'antd'
import TextArea from 'antd/lib/input/TextArea';
import { MenuItemType } from 'antd/lib/menu/interface';
import React, { useCallback, useEffect, useState } from 'react'

export function CommentExpandableFeedView({
    item, remove
}: {
    item: API.CommentVo,
    remove: (value: API.CommentVo) => void
}) {
    const [renderLookAll, setRenderLookAll] = useState<boolean>(true);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [pageResp, setPageResp] = useState<API.PageRespCommentVo | undefined>(undefined);
    let subList = item.subCommentList;
    if (subList && subList.length > 5) {
        subList = subList.slice(0, 5);
    }
    const [subCommentList, setSubCommentList] = useState(subList);

    const insertFirst = useCallback((e: API.CommentVo) => {
        setSubCommentList([e, ...(subCommentList ? subCommentList : [])]);
    }, [subCommentList]);


    const fetchData = async () => {
        setIsLoading(true);
        try {
            const resp = await listComment({
                commonPageReq: {
                    current: 1,
                },
                rootId: item.id
            });
            if (resp.code !== 0) {
                return;
            }
            const data = resp.data!.data!.filter(e => !subCommentList!.some(e2 => e2.id === e.id));
            if (data.length !== 0) {
                setPageResp({
                    ...resp.data,
                    data: data
                });
            } else if (resp.data!.current === resp.data!.pages) {
                setPageResp(undefined);
            } else {
                setPageResp({
                    ...resp.data,
                    data: []
                });
            }
            // } else if (resp.data!.current === resp.data!.pages) {
            //     setPageResp(undefined);
            // } else {
            //     const resp = await listComment({
            //         commonPageReq: {
            //             current: 2,
            //         },
            //         rootId: item.id
            //     });
            //     if (resp.code !== 0) {
            //         return;
            //     }
            //     if (data.length !== 0) {
            //         setPageResp({
            //             ...resp.data,
            //             data: data
            //         });
            //     } else {

            //     }
            // }
        } finally {
            setIsLoading(false);
            setRenderLookAll(false);
        }
    }

    return (
        <div style={{ paddingBottom: 10 }}>
            <CommentFeedView
                item={item} key={item.id}
                insertFirst={insertFirst}
                remove={(v) => {
                    if (v.id === item.id) {
                        remove(v);
                    }
                }}
            />
            {
                subCommentList && <div style={{
                    paddingLeft: 50,
                    paddingTop: 5,
                    width: "100%"
                }}>
                    <Space direction={"vertical"} size={"middle"} style={{ width: "100%" }}>
                        {subCommentList.map(e => <CommentFeedView
                            item={e}
                            key={e.id}
                            insertFirst={insertFirst}
                            remove={(v) => {
                                const i = subCommentList.findIndex(e => e.id === v.id);
                                if (i === -1) {
                                    console.log("找不到评论, " + JSON.stringify(subCommentList));
                                    return;
                                }
                                console.log("找到评论, 删除 " + JSON.stringify(subCommentList[i]));
                                setSubCommentList([
                                    ...subCommentList.slice(0, i),
                                    ...subCommentList.slice(i + 1, subCommentList.length),
                                ]);
                            }} />)}
                    </Space>
                    {
                        renderLookAll && item.subCommentList!.length > 5 &&
                        <Button
                            style={{
                                marginTop: 5,
                                marginLeft: 20
                            }}
                            onClick={fetchData}
                        >
                            查看更多回复 <ArrowDownOutlined />
                        </Button>
                    }
                    {
                        isLoading && (
                            <Spin />
                        )
                    }
                    {
                        pageResp && <CommentMoreableFeedView rootId={item.id!} pageResp={pageResp} insertFirst={(e) => {
                            setSubCommentList([e, ...subCommentList]);
                        }} />
                    }
                </div>
            }
        </div>
    );
}


function CommentMoreableFeedView({
    rootId, pageResp, insertFirst
}: {
    rootId: number;
    pageResp: API.PageRespCommentVo,
    insertFirst: (comment: API.CommentVo) => void,
}) {

    const [page, setPage] = useState(pageResp);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const fetchData = async () => {
        setIsLoading(true);
        try {
            const data = await listComment({
                commonPageReq: {
                    current: page.current! + 1,
                },
                rootId: rootId
            });

            if (data.code !== 0) {
                return;
            }
            if (data.data?.size !== 0) {
                const newPage = {
                    ...data.data!,
                    data: page.data?.concat(data.data!.data!)
                };
                console.log("newPage=", newPage);
                setPage(newPage);
            }
        } finally {
            setIsLoading(false);
        }
    }
    useEffect(() => {
        fetchData();
    }, []);

    return (
        <Space direction={"vertical"} size={"middle"} style={{ width: "100%" }}>
            {page.data!.map(e => <CommentFeedView
                item={e} key={e.id}
                insertFirst={insertFirst}
                remove={(v) => {
                    const list = page.data!;
                    const i = list.findIndex(e => e.id === v.id);
                    if (i === -1) {
                        return;
                    }
                    setPage({
                        ...page!,
                        data: [
                            ...list.slice(0, i),
                            ...list.slice(i, list.length),
                        ]
                    });
                }}
            />)}
            {
                page.current && page.pages && page.current != page.pages &&
                <Button
                    style={{
                        marginLeft: 20
                    }}
                    onClick={fetchData}
                >
                    加载更多
                </Button>
            }
            {
                isLoading && (
                    <Spin />
                )
            }
        </Space>
    );
}

function CommentFeedView({
    item, insertFirst, remove
}: {
    item: API.CommentVo;
    insertFirst: (comment: API.CommentVo) => void,
    remove: (comment: API.CommentVo) => void,
}) {
    const { initialState } = useModel('@@initialState');
    const [commentContent, setCommentContent] = useState("");
    const [isSubmittingComment, setIsSubmittingComment] = useState(false);
    const [isCommenting, setIsCommenting] = useState(false);
    const [isFavoring, setIsFavoring] = useState(false);
    const [isFavor, setIsFavor] = useState(item.isFavor!);
    const [favorCount, setFavorCount] = useState(item.favorCount!);
    const [replyCount, setReplyCount] = useState(item.replyCount!);
    const [isDeletingComment, setIsDeletingComment] = useState(false);

    const [isReportModalOpen, setIsReportModalOpen] = useState(false);
    const [form] = Form.useForm();

    const handleReport = async (values: { reason: string, description: string }) => {
        await reportComment(
            {
                commentId: item.id!,
            },
            values
        );
        setIsReportModalOpen(false);
        form.resetFields();
        notification.success({
            message: '举报成功',
            description: '感谢您的反馈，我们会尽快处理'
        });
    };

    const ReportModal = () => (
        <Modal
            title="举报内容"
            open={isReportModalOpen}
            onOk={() => form.submit()}
            onCancel={() => {
                setIsReportModalOpen(false);
                form.resetFields();
            }}
            okText="提交举报"
            cancelText="取消"
        >
            <Form
                form={form}
                onFinish={handleReport}
                layout="vertical"
            >
                <Form.Item
                    name="reason"
                    label="举报原因"
                    rules={[{ required: true, message: '请选择举报原因' }]}
                >
                    <Radio.Group>
                        <Space direction="vertical">
                            {REPORT_REASONS.map(reason => (
                                <Radio key={reason.value} value={reason.value}>
                                    <div>
                                        <div>{reason.label}</div>
                                        <div style={{ fontSize: '12px', color: '#999' }}>
                                            {reason.description}
                                        </div>
                                    </div>
                                </Radio>
                            ))}
                        </Space>
                    </Radio.Group>
                </Form.Item>
                <Form.Item
                    name="description"
                    label="补充说明"
                >
                    <Input.TextArea
                        placeholder="请详细描述举报原因（选填）"
                        maxLength={200}
                        showCount
                        rows={4}
                    />
                </Form.Item>
            </Form>
        </Modal>
    );


    return (<>
        <Card>
            <Space direction="vertical" size="middle" style={{ width: "100%" }}>
                <Space direction={"horizontal"} size={"large"}>
                    <Typography.Text>
                        {item.userNickName}@{item.username}{
                            item.replyToUserNickname && item.replyToUsername
                            && ` 回复给：${item.replyToUserNickname}@${item.replyToUsername}`
                        }
                    </Typography.Text>
                    {
                        item.rootCommentId === 0 && <Typography.Text >
                            评分：<span style={{ color: "red" }}>{commentScore(item.score!)}分</span>
                        </Typography.Text>
                    }
                    <Typography.Text>
                        {formatTimestamp(item.ctime)}
                    </Typography.Text>
                </Space>
                <Typography.Paragraph style={{ fontSize: 20 }}>
                    {item.content}
                </Typography.Paragraph>
                <Space direction={"horizontal"} size={"large"}>
                    {
                        isFavoring && <Spin />
                    }
                    {
                        !isFavoring && <span
                            style={{
                                cursor: "pointer"
                            }}
                            onClick={async () => {
                                if (!initialState?.currentUser) {
                                    history.push(loginPath);
                                    return;
                                }
                                setIsFavoring(true);
                                try {
                                    const resp = await favorComment({ id: item.id! });
                                    if (resp.code !== 0) {
                                        return;
                                    }
                                    if (resp.data) {
                                        setIsFavor(!isFavor);
                                        setFavorCount(!isFavor ? favorCount + 1 : favorCount - 1);
                                    }
                                } finally {
                                    setIsFavoring(false);
                                }
                            }}
                        >
                            <LikeOutlined
                                style={{
                                    color: isFavor ? "skyblue" : ""
                                }}
                            />
                            <Typography.Text
                                style={{
                                    color: isFavor ? "blue" : ""
                                }}
                            >
                                {favorCount === 0 ? "点赞" : favorCount}
                            </Typography.Text>
                        </span>
                    }
                    <span
                        style={{
                            cursor: "pointer",
                        }}
                        onClick={async () => {
                            if (!initialState?.currentUser) {
                                history.push(loginPath);
                                return;
                            }
                            setIsCommenting(!isCommenting);
                        }}
                    >
                        <CommentOutlined style={{
                            color: isCommenting ? "skyblue" : ""
                        }} />
                        <Typography.Text
                            style={{
                                color: isCommenting ? "skyblue" : ""
                            }}
                        >
                            {
                                !isCommenting && (
                                    replyCount === 0 ? "评论" : replyCount
                                )
                            }
                            {
                                isCommenting && "取消回复"
                            }
                        </Typography.Text>
                    </span>

                    <Button
                        color={"yellow"}
                        variant={"text"}
                        onClick={() => {
                            if (!initialState?.currentUser) {
                                history.push(loginPath);
                                return;
                            }
                            setIsReportModalOpen(true);
                        }}
                    >
                        <WarningOutlined />
                        <Typography.Text>
                            举报
                        </Typography.Text>
                    </Button >

                    {
                        initialState?.currentUser && initialState?.currentUser.id
                        === item.userId &&
                        <Button
                            color={"red"}
                            variant={"text"}
                            onClick={async () => {
                                setIsDeletingComment(true);
                                try {
                                    const resp = await deleteComment({ id: item.id! });
                                    if (resp.code === 0 && resp.data) {
                                        remove(item);
                                    }
                                } finally {
                                    setIsDeletingComment(false);
                                }
                            }}
                        >
                            {
                                isDeletingComment && <Spin />
                            }
                            {
                                !isDeletingComment && <>
                                    <DeleteOutlined style={{ color: "red" }} />
                                    <Typography.Text>
                                        删除
                                    </Typography.Text>
                                </>
                            }
                        </Button>
                    }
                </Space>
                {
                    isCommenting && <div>
                        <TextArea showCount
                            maxLength={500}
                            style={{
                                width: "100%"
                            }}
                            onChange={(e) => {
                                setCommentContent(e.target.value);
                            }}
                        />
                        <Flex justify={"end"} style={{ paddingTop: 25 }}>
                            {
                                !isSubmittingComment && <Button
                                    onClick={async () => {
                                        setIsSubmittingComment(true);
                                        try {
                                            const resp = await replyComment({
                                                apiId: item.apiId!,
                                                replyToCommentId: item.id,
                                                content: commentContent
                                            });
                                            if (resp.code !== 0) {
                                                return;
                                            }
                                            setIsCommenting(false);
                                            setReplyCount(replyCount + 1);
                                            insertFirst(resp.data!);
                                        } finally {
                                            setIsSubmittingComment(false);
                                        }
                                    }}
                                >提交</Button>
                            }
                            {
                                isSubmittingComment && <Spin />
                            }
                        </Flex>
                    </div>
                }
            </Space>
        </Card>
        <ReportModal />
    </>
    )
}

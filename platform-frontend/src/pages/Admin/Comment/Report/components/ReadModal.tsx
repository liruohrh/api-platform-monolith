
import { formatTimestamp } from '@/utils';
import { ProCard } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Button, Card, Divider, Flex, Modal, Rate, Space, Typography } from 'antd';
import React from 'react'
import StatusTag from './StatusTag';
import { commentScore } from '@/lib/utils/math';


type ReadModalProp = {
    open: boolean;
    record: API.CommentReportApplicationVo
    onCancel: () => void;
};
const ReadModal: React.FC<ReadModalProp> = ({
    open,
    record,
    onCancel,
}) => {
    return (
        <Modal
            title={"评论举报详情"}
            open={open}
            onCancel={() => { onCancel() }}
            width={"80%"}
            footer={false}
        >
            <ProCard>
                <Space direction={"vertical"} style={{ width: "100%" }}>
                    <Button type={"link"} style={{ padding: 0 }}>
                        <Link to={`/api/info/${record.apiId}`}>API：{record.apiName}</Link>
                    </Button>
                    <Typography.Title level={5}>
                        举报者：{record.reporterNickname}@ {record.reporterUsername}
                    </Typography.Title>
                    <Typography.Paragraph>
                        举报时间：{formatTimestamp(record.ctime)}
                    </Typography.Paragraph>
                    <Typography.Paragraph>
                        举报原因：{record.reason}
                    </Typography.Paragraph>
                    <Typography.Paragraph>
                        举报内容：{record.description}
                    </Typography.Paragraph>
                    <Typography.Paragraph>
                        审核状态：<StatusTag status={record.auditStatus!} />
                    </Typography.Paragraph>
                    <Typography.Paragraph>
                        审核回复：{record.replyContent ? record.replyContent : "无"}
                    </Typography.Paragraph>


                    <Card title="被举报的评论">
                        <Flex justify={"space-between"}>
                            <Typography.Title level={4}>
                                {record.reportedComment!.userNickName}@ {record.reportedComment!.username}
                            </Typography.Title>
                            {
                                record.reportedComment!.score !== undefined && record.reportedComment!.score !== 0 && <div>
                                    评分：<Rate allowHalf disabled value={commentScore(record.reportedComment!.score!)} />({commentScore(record.reportedComment!.score!)})
                                </div>
                            }
                            {
                                (record.reportedComment!.score === undefined || record.reportedComment!.score === 0) && <div>
                                    无评分(不是对API的评论)
                                </div>
                            }
                        </Flex>
                        <Typography.Paragraph>
                            评论内容：{record.reportedComment!.content}
                        </Typography.Paragraph>
                        <Flex justify={"space-between"}>
                            <Space direction={"horizontal"} size={"large"}>
                                <Space>
                                    <span>👍 {record.reportedComment!.favorCount || 0}</span>
                                    <span>💬 {record.reportedComment!.replyCount || 0}</span>
                                </Space>
                            </Space>
                            <Typography.Text type="secondary">
                                {formatTimestamp(record.reportedComment!.ctime)}
                            </Typography.Text>
                        </Flex>
                    </Card>
                    {/* <Typography.Title level={4}>
                        我的回复
                    </Typography.Title>
                    {
                        !record.adminReplies && '未回复'
                    }
                    {
                        record.adminReplies &&
                        <Space direction={"vertical"} style={{ width: "100%" }}>
                            {record.adminReplies.map((e, i) => (
                                <Space key={e.id} direction={"vertical"} style={{ width: "100%" }}>
                                    <Typography.Paragraph>
                                        {formatTimestamp(e.ctime)}
                                    </Typography.Paragraph>
                                    <Typography.Paragraph style={{ marginLeft: 20 }}>
                                        回复内容：{e.content}
                                    </Typography.Paragraph>
                                </Space>))}
                        </Space>
                    } */}
                </Space>
            </ProCard>
        </Modal>
    )
}

export default ReadModal

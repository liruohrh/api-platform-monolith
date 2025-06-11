
import { commentScore } from '@/lib/utils/math';
import { formatTimestamp } from '@/utils';
import { ProCard } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Button, Divider, Flex, Modal, Rate, Space, Typography } from 'antd';
import React from 'react'


type ReadModalProp = {
    open: boolean;
    record: API.CommentPageVo
    onCancel: () => void;
};
const ReadModal: React.FC<ReadModalProp> = ({
    open,
    record,
    onCancel,
}) => {
    return (
        <Modal
            title={"评论详情"}
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
                    <Flex justify={"space-between"}>
                        <Typography.Title level={4}>
                            {record.userNickname}@ {record.username}
                        </Typography.Title>
                        {
                            record.score !== undefined && record.score !== 0 && <div>
                                评分：<Rate allowHalf disabled value={commentScore(record.score!)} />({commentScore(record.score!)})
                            </div>
                        }
                        {
                            (record.score === undefined || record.score === 0) && <div>
                                无评分(不是对API的评论)
                            </div>
                        }
                    </Flex>
                    <Typography.Paragraph>
                        评论内容：{record.content}
                    </Typography.Paragraph>
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
                    <Divider />
                    <Typography.Title level={4}>
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
                    }
                </Space>
            </ProCard>
        </Modal>
    )
}

export default ReadModal

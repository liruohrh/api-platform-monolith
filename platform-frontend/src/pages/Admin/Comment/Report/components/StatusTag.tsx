import { Tag } from 'antd';
import React from 'react'

const StatusTag = ({ status }: { status: number }) => {
    switch (status) {
        case 0:
            return <Tag color="processing">待审核</Tag>;
        case 1:
            return <Tag color="success">已通过</Tag>;
        case 2:
            return <Tag color="error">未通过</Tag>;
        case 20:
            return <Tag color="error">取消申请</Tag>;
        default:
            return <Tag>未知状态</Tag>;
    }
}
export default StatusTag
import { ORDER_STATUS } from '@/lib/utils/order';
import { Tag } from 'antd';
import React from 'react'

const OrderStatusTag = ({ status }: { status: number }) => {
    switch (status) {
        case ORDER_STATUS.WAIT_PAY:
            return <Tag color="processing">待支付</Tag>;
        case ORDER_STATUS.PAID:
            return <Tag color="success">已支付</Tag>;
        case ORDER_STATUS.CANCEL:
            return <Tag color="error">已取消</Tag>;
        case ORDER_STATUS.REFUNDING:
            return <Tag color="processing">退款，待审核</Tag>;
        case ORDER_STATUS.REFUND_SUCCESS:
            return <Tag color="success">退款成功</Tag>;
        case ORDER_STATUS.REFUND_FAIL:
            return <Tag color="red">退款失败</Tag>;
        case ORDER_STATUS.REFUND_CANCEL:
            return <>
                <Tag color="success">已支付</Tag>
            </>;
        default:
            return <Tag>未知状态</Tag>;
    }
}
export default OrderStatusTag;
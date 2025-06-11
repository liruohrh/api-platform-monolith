import { REFUND_REASONS } from '@/lib/utils/order';
import { ModalForm } from '@ant-design/pro-components';
import { Form, Input, Radio, Space } from 'antd';
import React from 'react'




interface ReFundModalProp {
    open: boolean;
    onCancel: () => void;
    onOk: (values: Omit<API.OrderRefundReq, "orderId">) => Promise<void>;
}

const ReFundModal: React.FC<ReFundModalProp> = ({
    open, onCancel, onOk
}) => {
    return (
        <ModalForm
            clearOnDestroy
            title="退款申请"
            open={open}
            onFinish={async (values) => {
                onOk(values as Omit<API.OrderRefundReq, "orderId">);
            }}
            modalProps={{
                destroyOnClose: true
            }}
            onOpenChange={(v) => {
                if (!v) {
                    onCancel();
                }
            }}
        >
            <Form.Item
                name="reason"
                label="退款原因"
                rules={[{ required: true, message: '请选择退款原因' }]}
            >
                <Radio.Group>
                    <Space direction="vertical">
                        {REFUND_REASONS.map(reason => (
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
                    placeholder="请详细描述退款原因（选填）"
                    maxLength={200}
                    showCount
                    rows={4}
                />
            </Form.Item>
        </ModalForm>
    )
}

export default ReFundModal

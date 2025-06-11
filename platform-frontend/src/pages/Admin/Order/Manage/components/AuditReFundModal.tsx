import { REFUND_REASONS } from '@/lib/utils/order';
import { formatTimestamp } from '@/utils';
import { ModalForm, ProFormSelect } from '@ant-design/pro-components';
import { Card, Form, Input, Typography } from 'antd';
import React from 'react'

function auditStatusToString(status?: number): string {
    if (status === 0) {
        return "PENDING_AUDIT";
    }
    if (status === 1) {
        return "APPROVED";
    }
    if (status === 2) {
        return "NOT_PASS";
    }
    return "PENDING_AUDIT";
}

interface ReFundModalProp {
    open: boolean;
    username: string;
    userNickname: string;
    application: API.Application;
    onCancel: () => void;
    onOk: (values: API.ApplicationAuditReq) => Promise<void>;
}

const AuditReFundModal: React.FC<ReFundModalProp> = ({
    open, onCancel, onOk, application, userNickname, username
}) => {
    return (
        <ModalForm
            clearOnDestroy
            title="退款申请审核"
            open={open}
            onFinish={async (values) => {
                onOk(values as API.ApplicationAuditReq);
            }}
            modalProps={{
                destroyOnClose: true,
            }}
            onOpenChange={(v) => {
                if (!v) {
                    onCancel();
                }
            }}

        >
            <Card>
                <Typography.Paragraph>
                    退款人： {userNickname}@{username}
                </Typography.Paragraph>
                <Typography.Paragraph>
                    退款时间： {formatTimestamp(application?.ctime)}
                </Typography.Paragraph>
                <Typography.Paragraph>
                    退款原因： {REFUND_REASONS.find(e => e.value === application?.reason)?.label}
                </Typography.Paragraph>
                <Typography.Paragraph>
                    详细描述： {application?.description?.length !== 0
                        ? application.description
                        : REFUND_REASONS.find(e => e.value === application?.reason)?.description
                    }
                </Typography.Paragraph>
            </Card>
            <ProFormSelect
                name={"auditStatus"}
                initialValue={auditStatusToString(application?.auditStatus)}
                label={"审核状态"}
                valueEnum={{
                    "PENDING_AUDIT": {
                        text: '待审核',
                        status: 'default',
                    },
                    "APPROVED": {
                        text: '通过',
                        status: 'default',
                    },
                    "NOT_PASS": {
                        text: '拒绝',
                        status: 'default',
                    },
                }}
            />
            <Form.Item
                name="replyContent"
                label="回复"
                rules={[{ required: false, message: '请输入回复' }]}
            >
                <Input.TextArea defaultValue={application?.replyContent ?? ""} rows={4} placeholder="请输入回复" />
            </Form.Item>
        </ModalForm>
    )
}

export default AuditReFundModal

import { Form, Input, Modal, InputNumber, Radio, Checkbox } from 'antd';
import { useEffect } from 'react';

interface Props {
    open: boolean;
    onCancel: () => void;
    onOk: (values: API.UserApiUsageUpdateReq) => void;
    initialValues?: API.UserApiUsageUpdateReq;
    loading?: boolean;
}

const EditModal = ({ open, onCancel, onOk, initialValues, loading }: Props) => {
    const [form] = Form.useForm();

    useEffect(() => {
        if (open && initialValues) {
            form.setFieldsValue(initialValues);
        }
    }, [open, initialValues]);

    return (
        <Modal
            title="修改使用情况"
            open={open}
            onCancel={() => {
                form.resetFields();
                onCancel();
            }}
            onOk={() => {
                form.validateFields().then(onOk);
            }}
            confirmLoading={loading}
            destroyOnClose
        >
            <Form form={form} layout="vertical">
                <Form.Item
                    label="剩余次数"
                    name="leftTimes"
                    rules={[{ required: false, message: '请输入剩余次数' }]}
                >
                    <InputNumber min={0} style={{ width: '100%' }} />
                </Form.Item>
                <Form.Item
                    label="免费购买是否用过"
                    name="freeUsed"
                    valuePropName={"checked"}
                    rules={[{ required: false }]}
                >
                    <Checkbox />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default EditModal; 
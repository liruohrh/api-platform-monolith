export const REFUND_REASONS = [
  { label: '功能不符', value: 'functionality_mismatch', description: '购买的API功能与描述不符' },
  {
    label: '服务中断',
    value: 'service_disruption',
    description: '由于服务商问题造成功能不可用或服务中断',
  },
  { label: '数据错误', value: 'data_inaccuracy', description: '提供的数据存在错误或不准确' },
  { label: '未授权使用', value: 'unauthorized_use', description: '未经许可的数据访问或使用' },
  { label: '价格争议', value: 'pricing_dispute', description: '对费用、隐藏收费或价格变动有异议' },
  {
    label: '技术支持不足',
    value: 'insufficient_support',
    description: '未能获得应有的技术支持或帮助',
  },
  { label: '其他原因', value: 'other', description: '其他导致请求退款的原因' },
];

export function canComment(status: number) {
  return [ORDER_STATUS.PAID, ORDER_STATUS.REFUND_CANCEL, ORDER_STATUS.REFUND_FAIL].includes(status);
}
export function canPlay(status: number) {
  return [ORDER_STATUS.WAIT_PAY].includes(status);
}
export function canCancel(status: number) {
  return ![ORDER_STATUS.CANCEL].includes(status) && [ORDER_STATUS.WAIT_PAY].includes(status);
}
export function canCancelRefund(status: number) {
  return (
    ![ORDER_STATUS.REFUND_CANCEL].includes(status) && [ORDER_STATUS.REFUNDING].includes(status)
  );
}
export function canRefund(status: number, actualPayment: number) {
  return (
    actualPayment !== 0 &&
    ![ORDER_STATUS.REFUNDING, ORDER_STATUS.REFUND_SUCCESS, ORDER_STATUS.REFUND_FAIL].includes(
      status,
    ) &&
    [ORDER_STATUS.PAID, ORDER_STATUS.REFUND_CANCEL].includes(status)
  );
}
export function canAuditRefund(status: number) {
  return [ORDER_STATUS.REFUNDING, ORDER_STATUS.REFUND_SUCCESS, ORDER_STATUS.REFUND_FAIL].includes(
    status,
  );
}
export const ORDER_STATUS_ANT_VALUE_ENUM = {
  0: {
    text: '等待支付',
  },
  1: {
    text: '已支付',
  },
  2: {
    text: '未支付',
  },
  40: {
    text: '退款，待审核',
  },
  41: {
    text: '退款成功',
  },
  42: {
    text: '退款失败',
  },
  43: {
    text: '取消退款',
  },
};
export const ORDER_STATUS = {
  WAIT_PAY: 0,
  PAID: 1,
  CANCEL: 2,
  REFUNDING: 40,
  REFUND_SUCCESS: 41,
  REFUND_FAIL: 42,
  REFUND_CANCEL: 43,
};

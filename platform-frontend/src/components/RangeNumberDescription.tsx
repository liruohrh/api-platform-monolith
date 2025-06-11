
import { Typography } from 'antd'
import React from 'react'

export default function RangeNumberDescription() {
    return (
        <Typography.Paragraph type={"secondary"} style={{ paddingLeft: 20 }}>
            单价输入0查询免费的，创建时间、更新时间、实际支付可以输入范围查询。<br />
            语法（就是数学用的范围语法--区间）： 如[1,2]、 (1,2)、 [1,2)、 (1,2]、 [1,)、 (1,)、 (,2]、(,2)。<br />
            闭区间（方括号）表示相等，开区间（圆括号）表示不相等。<br />
            注意：时间有毫秒，推荐在小于时多加1s。
        </Typography.Paragraph>
    )
}

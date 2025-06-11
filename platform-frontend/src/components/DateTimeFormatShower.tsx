import { parseToTimestamp } from '@/utils';
import { Input, Space, Typography } from 'antd'
import React, { useEffect, useState } from 'react'



export default function DateTimeFormatShower() {
    const [inputDateTime, setInputDateTime] = useState("");
    const [unixMills, setUnixMills] = useState(0);
    const [errorMessage, setErrorMessage] = useState("");

    useEffect(() => {
        if (!inputDateTime) {
            return;
        }
        try {
            setUnixMills(parseToTimestamp(inputDateTime));
            setErrorMessage("");
        } catch (e) {
            setErrorMessage((e as Error).message);
        }
    }, [inputDateTime]);

    return (

        <Space direction={"vertical"} style={{ width: "100%" }}>
            <Typography.Title level={5}>时间转换器：</Typography.Title>
            <div style={{ paddingLeft: 20 }}>
                <Typography.Paragraph>格式：`2024-01-01 00:00:00`</Typography.Paragraph>
                <Space direction={"horizontal"} size={"middle"} style={{ width: "100%" }}>
                    <Input placeholder="输入时间"
                        onChange={(e) => {
                            setInputDateTime(e.target.value);
                        }}
                    />
                    <div>
                        结果：
                        {
                            unixMills && <Typography.Paragraph copyable>
                                {unixMills}
                            </Typography.Paragraph>
                        }
                    </div>
                </Space>
                {
                    errorMessage && <Typography.Paragraph
                        style={{ paddingLeft: 20 }}
                        type={"danger"}
                    >{errorMessage}
                    </Typography.Paragraph>
                }
            </div>
        </Space>
    )
}

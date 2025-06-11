import React from 'react'
import AlignLeftText, { AlignLeftTextProps } from './AlignLeftText';
import { Space } from 'antd';

interface Props {
    wordSize: number;
    endsWithColon?: boolean;
    items: AlignLeftTextProps[];
}
const AlignLeftTexts: React.FC<Props> = ({ items, ...props }) => {
    return (
        <Space direction={"vertical"} size={"middle"}>
            {items.map(e => {
                const { children, ...other } = e;
                return <AlignLeftText
                    key={e.label}
                    {
                    ...props
                    }
                    {
                    ...other
                    }
                >
                    {children}
                </AlignLeftText >;
            })
            }
        </Space>
    )
}

export default AlignLeftTexts;

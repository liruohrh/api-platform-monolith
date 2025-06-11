import { ArrowDownOutlined, ArrowUpOutlined } from "@ant-design/icons";
import { Button } from "antd";
import React, { CSSProperties, useState } from "react";
const getSortVal = (isUp: boolean | undefined): string | undefined => {
    if (isUp === undefined) {
        return undefined;
    } else if (isUp === true) {
        return 'ASC';
    } else if (isUp === false) {
        return 'DESC';
    }
}
const SortButton: React.FC<{
    sortField: {
        title: string;
        name: string;
        sort?: string;
        isUp?: boolean;
    }
    style?: CSSProperties | undefined;
}> = ({
    sortField,
    style,
}) => {
        const [isUp, setUp] = useState<boolean | undefined>(sortField.isUp);
        return (
            <Button
                style={style}
                onClick={() => {
                    let newIsUp: boolean | undefined;
                    if (isUp === undefined) {
                        newIsUp = true;

                    } else if (isUp === true) {
                        newIsUp = false;

                    } else if (isUp === false) {
                        newIsUp = undefined;

                    }
                    setUp(newIsUp);
                    sortField.sort = getSortVal(newIsUp)
                }}
            >
                {sortField.title}
                {isUp === undefined &&
                    (<><ArrowUpOutlined /><ArrowDownOutlined /></>)
                }
                {isUp === true &&
                    (<><ArrowUpOutlined /></>)
                }
                {isUp === false &&
                    (<><ArrowDownOutlined /></>)
                }
            </Button>
        )
    }
export default SortButton;
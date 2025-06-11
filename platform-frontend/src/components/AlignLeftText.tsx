import React, { CSSProperties } from 'react'

export interface AlignLeftTextProps {
    wordSize?: number;
    label: string;
    children: React.ReactNode;
    endsWithColon?: boolean;
    strong?: boolean;
}
const AlignLeftText: React.FC<AlignLeftTextProps> = ({ wordSize = 5, label, children, endsWithColon = true, strong }) => {
    const styles: CSSProperties = {};

    if (strong) {
        console.log(label, strong);
        styles.fontWeight = "bold";
    }
    return (
        <div style={{
            display: "flex",
            flexDirection: "row",
            justifyContent: "start",
            ...styles
        }}
        >
            <div style={{ width: wordSize + "rem" }}>{label}{endsWithColon ? "：" : ""}</div>
            <div>
                {children}
            </div>
        </div>
    )
}

export default AlignLeftText;

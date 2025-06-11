import { ArrowDownOutlined, ArrowUpOutlined } from "@ant-design/icons";

export default function SortIcon({ value }: { value?: boolean }) {
    if (value === undefined) {
        return <><ArrowUpOutlined /><ArrowDownOutlined /></>;
    }
    if (value === true) {
        return <ArrowUpOutlined />;
    }
    return <ArrowDownOutlined />;
}
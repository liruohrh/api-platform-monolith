

import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Dropdown, Flex, notification, Space, Typography } from 'antd';
import React, { useRef, useState } from 'react';
import { formatTimestamp, getSamePropButValueNotEquals, parseToTimestamp } from '@/utils';
import { DeleteFilled, MoreOutlined, PlusOutlined } from '@ant-design/icons';
import { addApi, listApi, updateApi, getApiById, rollOffApi, launchApi, deleteApi } from '@/services/api-platform/apiController';
import UpdateModalForm from './components/UpdateModalForm';
import ReadModal from './components/ReadModal';
import AddModalForm from './components/AddModalForm';
import { Link } from '@umijs/max';
import { mappingDateTimeRange, mappingSort } from '@/lib/utils/ant';

const AdminAPI: React.FC = () => {
    const [readModalVisible, setReadModalVisible] = useState<boolean>(false);
    const [addModalVisible, setAddModalVisible] = useState<boolean>(false);
    const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
    const actionRef = useRef<ActionType>();
    const [currentRow, setCurrentRow] = useState<API.HttpApiResp>();

    const columns: ProColumns<API.HttpApiResp>[] = [
        {
            title: 'ID',
            dataIndex: 'id',
            render(dom, entity, index, action, schema) {
                return <Link to={`/api/info/${entity.id}`}>{entity.id}</Link>
            },
        },
        {
            title: '名称',
            dataIndex: 'name',
            width: 250,
            render(dom, entity, index, action, schema) {
                return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {entity.name}
                </div>
            },
        },
        {
            title: 'logo',
            dataIndex: 'logoUrl',
            valueType: 'image',
            search: false,
            hideInTable: true,
        },
        {
            title: '描述',
            dataIndex: 'description',
            valueType: "text",
            hideInTable: true,
        },

        {
            title: '方法',
            dataIndex: 'method',
            valueEnum: {
                "GET": {
                    text: 'GET',
                },
                "POST": {
                    text: 'POST',
                },
                "PUT": {
                    text: 'PUT',
                },
                "DELETE": {
                    text: 'DELETE',
                },
            },
            hideInTable: true,
        },
        {
            title: '协议',
            dataIndex: 'protocol',
            valueEnum: {
                "http": {
                    text: 'http',
                },
                "https": {
                    text: 'https',
                }
            },
            hideInTable: true,
        },
        {
            title: '域名',
            dataIndex: 'domain',
            valueType: 'text',
            hideInTable: true,
        },
        {
            title: '路径',
            dataIndex: 'path',
            valueType: 'text',
            hideInTable: true,
        },
        {
            title: '价格(元)',
            dataIndex: 'price',
            search: false,
            render: (dom, entity) => {
                if (entity.price === 0) {
                    return (
                        <Typography.Text>免费</Typography.Text>
                    );
                } else {
                    return (
                        <Typography.Text>{entity.price?.toFixed(5)}</Typography.Text>
                    );
                }
            },
        },
        {
            title: '免费次数',
            dataIndex: 'freeTimes',
            valueType: "digit",
            search: false,
            render: (dom, entity) => {
                if (entity.price === 0) {
                    return (
                        <Typography.Text>无限</Typography.Text>
                    );
                } else {
                    return (
                        <Typography.Text>{entity.freeTimes}</Typography.Text>
                    );
                }
            },
        },
        {
            title: '状态',
            dataIndex: 'status',
            valueEnum: {
                0: {
                    text: '待审核',
                    status: 'processing'
                },
                1: {
                    text: '上线',
                    status: 'success',
                },
                2: {
                    text: '下线',
                    status: "default",
                },
                3: {
                    text: '被禁用',
                    status: 'error',
                },
            },
        },
        {
            title: '评分',
            dataIndex: 'score',
            valueType: 'text',
            search: false,
            sorter: true,
            width: 60,
        },
        {
            title: '订单量',
            dataIndex: 'orderVolume',
            valueType: 'text',
            search: false,
            sorter: true,
            hideInTable: true
        },
        {
            title: '创建时间',
            dataIndex: 'ctime',
            valueType: 'dateTimeRange',
            sorter: true,
            width: 150,
            render(dom, entity, index, action, schema) {
                return formatTimestamp(entity.ctime);
            },
        },
        {
            title: '更新时间',
            dataIndex: 'utime',
            valueType: 'dateTimeRange',
            search: false,
            sorter: true,
            render(dom, entity, index, action, schema) {
                return formatTimestamp(entity.ctime);
            },
            hideInTable: true,
        },
        {
            title: '操作',
            width: 200,
            render: (_, entity, index, action) => {
                return (
                    <Space direction={"horizontal"} size={8}>
                        <a
                            onClick={() => {
                                setReadModalVisible(true);
                                setCurrentRow(entity);
                            }}
                        >
                            查看
                        </a>
                        <a
                            onClick={() => {
                                setUpdateModalVisible(true);
                                setCurrentRow(entity);

                            }}
                        >
                            修改
                        </a>
                        <a
                            onClick={async () => {
                                const resp = await deleteApi({ apiId: entity.id!! });
                                if (resp.code !== 0) {
                                    return;
                                }
                                action?.reload();
                            }}
                        >
                            删除<DeleteFilled style={{ color: "red" }} />
                        </a>
                    </Space>
                )
            },
        },
    ];


    return (
        <PageContainer title={false}>
            <ProTable<API.HttpApiResp>
                pagination={{
                    pageSize: 5,
                }}
                headerTitle={'API信息列表'}
                actionRef={actionRef}
                rowKey="id"
                cardBordered
                search={{
                    labelWidth: 120,
                }}
                //@ts-ignore
                request={async (params, sort, filter) => {
                    const mSort: Record<string, boolean> = sort ? mappingSort(sort) : {};
                    const mParams = mappingDateTimeRange(params);
                    const data = await listApi({
                        req: {
                            ...mParams,
                            ...mSort,
                        }
                    });
                    return {
                        data: data.data?.data,
                        success: data.code === 0,
                        total: data.data?.total,
                    };
                }}
                toolbar={{
                    settings: [],
                    actions: [
                        <Button
                            type="primary"
                            key="primary"
                            onClick={() => {
                                setCurrentRow(undefined);
                                setAddModalVisible(true);
                            }}
                        >
                            <PlusOutlined /> 新建
                        </Button>,
                    ]
                }}
                columns={columns.map(e => ({
                    ...e,
                    align: "center",
                }))}
            />
            {
                addModalVisible && <AddModalForm
                    handlerSubmit={async (values: API.HttpApiAddReq): Promise<void> => {
                        const data = await addApi({ ...values });
                        notification.success({ message: "添加成功！" });
                        setCurrentRow((await getApiById({ apiId: data.data!! })).data);
                        actionRef.current?.reload();
                        setReadModalVisible(true);
                        setAddModalVisible(false);

                    }}
                    handlerCloseModal={() => {
                        setAddModalVisible(false);
                    }}
                    isOpenModal={addModalVisible}
                />
            }

            {
                readModalVisible && <ReadModal
                    handlerCloseModal={() => {
                        setReadModalVisible(false);
                    }}
                    open={readModalVisible}
                    record={currentRow!!}
                />
            }
            {
                updateModalVisible && <UpdateModalForm
                    handlerSubmit={async (values: API.HttpApiUpdateReq): Promise<void> => {
                        const resp = await updateApi({
                            ...getSamePropButValueNotEquals(values, currentRow),
                            id: values.id
                        });
                        if (resp.code !== 0) {
                            return;
                        }
                        // setCurrentRow((await getApiById({ apiId: values.id })).data);
                        actionRef.current?.reload();
                        //更新完打开只读Modal
                        setUpdateModalVisible(false);
                    }}
                    handlerCloseModal={() => {
                        setUpdateModalVisible(false);
                    }}
                    isOpenModal={updateModalVisible}
                    values={currentRow!!}
                />
            }
        </PageContainer>
    );
};
export default AdminAPI;

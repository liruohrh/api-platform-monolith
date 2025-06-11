import UpdateForm from '@/pages/Admin/User/components/UpdateForm';
import { listUser } from '@/services/api-platform/userController';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import { PageContainer, ProDescriptions, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Drawer, message, notification } from 'antd';
import React, { useRef, useState } from 'react';
import { formatTimestamp, } from '@/utils';
import { mappingDateTimeRange, mappingSort } from '@/lib/utils/ant';
import AddModal from './components/AddModal';
import { addUser } from '@/services/api-platform/adminUserController';

const AdminUserPage: React.FC = () => {
  const [addModalVisible, setAddModalVisible] = useState(false);
  const [isOpenUpdateModal, setOpenUpdateModal] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.UserVo>();

  const columns: ProColumns<API.UserVo>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      valueType: 'text',
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 150,
      render(dom, entity, index, action, schema) {
        return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
          {entity.username}
        </div>
      },
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      width: 150,
      render(dom, entity, index, action, schema) {
        return <div style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
          {entity.nickname}
        </div>
      },
    },
    {
      title: '头像',
      dataIndex: 'avatarUrl',
      valueType: 'image',
      search: false,
      hideInTable: true,
    },
    {
      title: '角色',
      dataIndex: 'role',
      valueEnum: {
        USER: {
          text: '普通用户',
          status: 'Default'
        },
        ADMIN: {
          text: '管理员',
          status: 'Success',
        },
      },
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      valueType: "text",
      width: 200,
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: {
        0: {
          text: '正常',
          status: 'Success',
        },
        1: {
          text: '冻结',
          status: 'Warning',
        },
      },
    },
    {
      title: 'AppKey',
      dataIndex: 'appKey',
      valueType: "text",
      hideInTable: true
    },
    {
      title: '个人描述',
      dataIndex: 'personalDescription',
      valueType: "text",
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
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        return [
          <Button
            disabled={showDetail}
            color="primary" variant="link"
            onClick={() => {
              setCurrentRow(record);
              setShowDetail(true);
            }}
          >
            查看
          </Button>,
          <Button
            color="primary" variant="link"
            onClick={() => {
              setOpenUpdateModal(true);
              setCurrentRow(record);
            }}
          >
            修改
          </Button>
        ];
      },
    }
  ];

  return (
    <PageContainer title={false}>
      <ProTable<API.UserVo>
        pagination={{
          pageSize: 5,
        }}
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
          try {
            const data = await listUser({
              req: {
                ...mSort,
                ...mParams
              }
            });

            return {
              data: data.data?.data,
              success: true,
              total: data.data?.total,
            };
          } catch (e) {
            return {
              success: false,
            };
          }
        }}
        columns={columns.map(e => ({
          ...e,
          align: "center",
        }))}

        toolbar={{
          settings: [],
          actions: [
            <Button
              variant={"solid"}
              color={"primary"}
              onClick={() => {
                setAddModalVisible(true);
              }}
            >
              添加
            </Button>
          ]
        }}
      />

      {
        addModalVisible && <AddModal
          open={addModalVisible}
          onOk={async (values: API.UserAddReq): Promise<void> => {
            const data = await addUser(values);
            if (data.code !== 0) {
              notification.error({ message: `新增失败！${data.msg}` });
              return;
            }
            notification.success({ message: "新增成功！" });
            setAddModalVisible(false);
            await actionRef.current?.reload();
          }}
          onCancel={() => {
            setAddModalVisible(false);
          }}
        />
      }
      {
        isOpenUpdateModal && <UpdateForm
          onFormFinish={async (submitSuccess: boolean): Promise<void> => {
            if (submitSuccess) {
              setOpenUpdateModal(false);
              setCurrentRow(undefined);
              await actionRef.current?.reload();
            }
          }}
          handlerCloseModal={() => {
            setOpenUpdateModal(false);
            if (!showDetail) {
              setCurrentRow(undefined);
            }
          }}
          isOpenUpdateModal={isOpenUpdateModal}
          values={currentRow || {}}
        />
      }

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.id && (
          <ProDescriptions<API.UserVo>
            column={2}
            title={currentRow?.username}
            request={async () => ({
              data: currentRow!,
            })}
            columns={[
              ...(columns as ProDescriptionsItemProps<API.UserVo>[])]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};
export default AdminUserPage;

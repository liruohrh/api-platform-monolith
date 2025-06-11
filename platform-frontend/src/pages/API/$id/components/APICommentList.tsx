import { listComment } from '@/services/api-platform/commentController';
import { ActionType, PageContainer, ProList } from '@ant-design/pro-components';
import React, { useRef, useState } from 'react'
import { CommentExpandableFeedView } from './CommentFeedView';

export default function APICommentList({
    apiId
}: {
    apiId: number
}) {
    const [total, setTotal] = useState<number>(0);
    const proListRef = useRef<ActionType | undefined>();
    return (
        <PageContainer title={`评论（${total}）`}>
            <ProList<API.CommentVo>
                actionRef={proListRef}
                rowKey={record => record.id!!}
                pagination={{
                    pageSize: 20,
                }}
                request={async (params) => {
                    try {
                        const data = await listComment({
                            commonPageReq: {
                                current: params.current!,
                            },
                            apiId: apiId,
                        });
                        if (total === 0) {
                            setTotal(data.data?.total!);
                        }
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
                renderItem={(item: API.CommentVo) => {
                    return (
                        <CommentExpandableFeedView item={item} remove={(_) => {
                            proListRef.current?.reload();
                        }} />
                    )
                }}
            />
        </PageContainer >
    )
}

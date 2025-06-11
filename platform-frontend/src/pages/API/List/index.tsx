import SortIcon from '@/components/SortIcon';
import { apiScore } from '@/lib/utils/math';
import { searchApi } from '@/services/api-platform/apiController';
import { formatTimestamp, toggleSortValue } from '@/utils';
import { ActionType, PageContainer, ProCard, ProList } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Button, Card, Popover, Space, Typography, Image, Tag, Checkbox, Divider, Flex } from 'antd';
import Search from 'antd/lib/input/Search';
import React, { useEffect, useRef, useState } from 'react';

const PAGE_SIZE = 6;
type Filters = Omit<API.ApiSearchReq, "current">;

const APIListPage: React.FC = () => {
  const [current, setCurrent] = useState(1);
  const [filters, setFilters] = useState<Filters>({});
  const [currentFilters, setCurrentFilters] = useState<Filters>({});
  const actionRef = useRef<ActionType>();
  const [isLoading, setIsLoading] = useState<boolean>(false);


  useEffect(() => {
    actionRef.current?.reload();
  }, [current, currentFilters]);

  const reSearch = (values: Partial<Filters>) => {
    const filters0 = { ...filters, ...values };
    setFilters(filters0);
    setCurrent(1);
    setCurrentFilters(filters0);
  }

  return (
    <PageContainer>
      <ProCard style={{ width: "100%" }}>
        <Space direction={"vertical"} style={{ width: "100%" }}>
          <ProCard >
            <Popover title="可以根据名称、描述搜索">
              <Search
                loading={isLoading}
                enterButton
                onSearch={async (key) => {
                  reSearch({ key });
                }}
              />
            </Popover>
          </ProCard>
          <ProCard >
            <Space direction={"horizontal"} style={{ width: "100%" }}>
              <Button
                onClick={() => {
                  reSearch({ price: toggleSortValue(filters.price) });
                }}
              >
                价格<SortIcon value={filters.price} />
              </Button>
              <Button
                onClick={() => {
                  reSearch({ orderVolume: toggleSortValue(filters.orderVolume) });
                }}
              >
                成交量<SortIcon value={filters.orderVolume} />
              </Button>
              <Button
                onClick={() => {
                  reSearch({ score: toggleSortValue(filters.score) });
                }}
              >
                评分<SortIcon value={filters.score} />
              </Button>
              <Button
                onClick={() => {
                  reSearch({ score: toggleSortValue(filters.ctime) });
                }}
              >
                创建时间<SortIcon value={filters.ctime} />
              </Button>
            </Space>
            <Divider />
            <Space direction={"horizontal"} style={{ width: "100%" }}>
              <Checkbox
                checked={filters.isFree}
                onChange={(e) => {
                  reSearch({ isFree: e.target.checked });
                }}
              >
                免费
              </Checkbox>
            </Space>
            <Flex justify={"end"}>
              <Button
                color={"primary"}
                variant={"solid"}
                onClick={() => {
                  setFilters({});
                  setCurrent(1);
                  setCurrentFilters({});
                }}>
                重置查询条件
              </Button>
            </Flex>
          </ProCard >
        </Space>
      </ProCard >
      <ProList<API.ApiSearchVo>
        rowKey={record => record.id!!}
        actionRef={actionRef}
        pagination={{
          pageSize: PAGE_SIZE,
        }}
        request={async (params) => {
          setIsLoading(true);
          setCurrent(params.current!);
          try {
            const data = await searchApi({
              req: {
                ...currentFilters,
                current: params.current!,
              }
            });
            return {
              data: data.data?.data,
              success: data.code === 0,
              total: data.data?.total,
            };
          } finally {
            setIsLoading(false);
          }
        }}
        grid={{ gutter: 16, column: 3 }}
        renderItem={(apiSearchVo: API.ApiSearchVo) => {
          return (
            <Link to={`/api/info/${apiSearchVo.id}`}>
              <Card
                style={{ margin: "10px" }}
              >
                <Image src={apiSearchVo.logoUrl} width={50} height={50} />
                <Typography.Title level={5}>
                  {apiSearchVo.name}
                </Typography.Title>
                <Space direction={"vertical"} style={{ width: "100%" }}>
                  <Space direction={"horizontal"} style={{ width: "100%" }}>
                    <Tag color="green">{apiSearchVo.price === 0 ? "免费" : `价格: ${apiSearchVo.price?.toFixed(5)} 元`}</Tag>
                    <Tag color="gold">评分:{apiScore(apiSearchVo.score!)}</Tag>
                    <Tag color="volcano">成交量:{apiSearchVo.orderVolume}</Tag>
                  </Space>
                  <Space direction={"vertical"} style={{ width: "100%" }}>
                    <Tag color="geekblue">创建时间：{formatTimestamp(apiSearchVo.ctime)}</Tag>
                  </Space>
                  <Typography.Paragraph>
                    <Typography.Text>描述:
                    </Typography.Text>
                    {apiSearchVo.description}
                  </Typography.Paragraph>
                </Space>
              </Card>
            </Link>
          )
        }}
      />
    </PageContainer >
  );
}
export default APIListPage;

import type { ActionType, ProColumns } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { message, Button, Space, Popconfirm, Input, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import type { Dispatch } from 'umi';
import { StatusEnum } from '../enum';
import { connect } from 'umi';
import type { StateType } from '../model';
import { SENSITIVE_LEVEL_ENUM } from '../constant';
import {
  queryMetric,
  deleteMetric,
  batchUpdateMetricStatus,
  batchDownloadMetric,
} from '../service';

import MetricInfoCreateForm from './MetricInfoCreateForm';
import BatchCtrlDropDownButton from '@/components/BatchCtrlDropDownButton';
import moment from 'moment';
import styles from './style.less';
import { ISemantic } from '../data';

type Props = {
  dispatch: Dispatch;
  domainManger: StateType;
};

const ClassMetricTable: React.FC<Props> = ({ domainManger, dispatch }) => {
  const { selectModelId: modelId, selectDomainId } = domainManger;
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  const [metricItem, setMetricItem] = useState<ISemantic.IMetricItem>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0,
  });
  const actionRef = useRef<ActionType>();

  const [downloadLoading, setDownloadLoading] = useState<boolean>(false);

  const queryBatchUpdateStatus = async (ids: React.Key[], status: StatusEnum) => {
    if (Array.isArray(ids) && ids.length === 0) {
      return;
    }
    const { code, msg } = await batchUpdateMetricStatus({
      ids,
      status,
    });
    if (code === 200) {
      actionRef?.current?.reload();
      dispatch({
        type: 'domainManger/queryMetricList',
        payload: {
          modelId,
        },
      });
      return;
    }
    message.error(msg);
  };

  const queryMetricList = async (params: any) => {
    const { code, data, msg } = await queryMetric({
      ...params,
      ...pagination,
      modelId,
    });
    const { list, pageSize, pageNum, total } = data || {};
    let resData: any = {};
    if (code === 200) {
      setPagination({
        ...pagination,
        pageSize: Math.min(pageSize, 100),
        current: pageNum,
        total,
      });

      resData = {
        data: list || [],
        success: true,
      };
    } else {
      message.error(msg);
      resData = {
        data: [],
        total: 0,
        success: false,
      };
    }
    return resData;
  };

  const columns: ProColumns[] = [
    {
      dataIndex: 'id',
      title: 'ID',
      width: 80,
      search: false,
    },
    {
      dataIndex: 'name',
      title: '指标名称',
      search: false,
    },
    {
      dataIndex: 'key',
      title: '指标搜索',
      hideInTable: true,
      renderFormItem: () => <Input placeholder="请输入ID/指标名称/字段名称/标签" />,
    },
    {
      dataIndex: 'alias',
      title: '别名',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      dataIndex: 'bizName',
      title: '字段名称',
      search: false,
    },
    {
      dataIndex: 'sensitiveLevel',
      title: '敏感度',
      width: 80,
      valueEnum: SENSITIVE_LEVEL_ENUM,
    },
    {
      dataIndex: 'status',
      title: '状态',
      width: 80,
      search: false,
      render: (status) => {
        switch (status) {
          case StatusEnum.ONLINE:
            return <Tag color="success">已启用</Tag>;
          case StatusEnum.OFFLINE:
            return <Tag color="warning">未启用</Tag>;
          case StatusEnum.INITIALIZED:
            return <Tag color="processing">初始化</Tag>;
          case StatusEnum.DELETED:
            return <Tag color="default">已删除</Tag>;
          default:
            return <Tag color="default">未知</Tag>;
        }
      },
    },
    {
      dataIndex: 'createdBy',
      title: '创建人',
      width: 100,
      search: false,
    },
    {
      dataIndex: 'tags',
      title: '标签',
      search: false,
      render: (tags) => {
        if (Array.isArray(tags)) {
          return (
            <Space size={2} wrap>
              {tags.map((tag) => (
                <Tag color="blue" key={tag}>
                  {tag}
                </Tag>
              ))}
            </Space>
          );
        }
        return <>--</>;
      },
    },
    {
      dataIndex: 'description',
      title: '描述',
      search: false,
    },
    {
      dataIndex: 'updatedAt',
      title: '更新时间',
      width: 180,
      search: false,
      render: (value: any) => {
        return value && value !== '-' ? moment(value).format('YYYY-MM-DD HH:mm:ss') : '-';
      },
    },
    {
      title: '操作',
      dataIndex: 'x',
      valueType: 'option',
      width: 150,
      render: (_, record) => {
        return (
          <Space className={styles.ctrlBtnContainer}>
            <Button
              type="link"
              key="metricEditBtn"
              onClick={() => {
                setMetricItem(record);
                setCreateModalVisible(true);
              }}
            >
              编辑
            </Button>
            {record.status === StatusEnum.ONLINE ? (
              <Button
                type="link"
                key="editStatusOfflineBtn"
                onClick={() => {
                  queryBatchUpdateStatus([record.id], StatusEnum.OFFLINE);
                }}
              >
                停用
              </Button>
            ) : (
              <Button
                type="link"
                key="editStatusOnlineBtn"
                onClick={() => {
                  queryBatchUpdateStatus([record.id], StatusEnum.ONLINE);
                }}
              >
                启用
              </Button>
            )}
            <Popconfirm
              title="确认删除？"
              okText="是"
              cancelText="否"
              onConfirm={async () => {
                const { code, msg } = await deleteMetric(record.id);
                if (code === 200) {
                  setMetricItem(undefined);
                  actionRef.current?.reload();
                } else {
                  message.error(msg);
                }
              }}
            >
              <Button
                type="link"
                key="metricDeleteBtn"
                onClick={() => {
                  setMetricItem(record);
                }}
              >
                删除
              </Button>
            </Popconfirm>
          </Space>
        );
      },
    },
  ];

  const rowSelection = {
    onChange: (selectedRowKeys: React.Key[]) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  };

  const onMenuClick = (key: string) => {
    switch (key) {
      case 'batchStart':
        queryBatchUpdateStatus(selectedRowKeys, StatusEnum.ONLINE);
        break;
      case 'batchStop':
        queryBatchUpdateStatus(selectedRowKeys, StatusEnum.OFFLINE);
        break;
      default:
        break;
    }
  };

  const downloadMetricQuery = async (
    ids: React.Key[],
    dateStringList: string[],
    pickerType: string,
  ) => {
    if (Array.isArray(ids) && ids.length > 0) {
      setDownloadLoading(true);
      const [startDate, endDate] = dateStringList;
      await batchDownloadMetric({
        metricIds: ids,
        dateInfo: {
          dateMode: 'BETWEEN',
          startDate,
          endDate,
          period: pickerType.toUpperCase(),
        },
      });
      setDownloadLoading(false);
    }
  };

  return (
    <>
      <ProTable
        className={`${styles.classTable} ${styles.classTableSelectColumnAlignLeft}`}
        actionRef={actionRef}
        rowKey="id"
        search={{
          span: 4,
          defaultCollapsed: false,
          collapseRender: () => {
            return <></>;
          },
        }}
        rowSelection={{
          type: 'checkbox',
          ...rowSelection,
        }}
        columns={columns}
        params={{ modelId }}
        request={queryMetricList}
        pagination={pagination}
        tableAlertRender={() => {
          return false;
        }}
        onChange={(data: any) => {
          const { current, pageSize, total } = data;
          setPagination({
            current,
            pageSize,
            total,
          });
        }}
        size="small"
        options={{ reload: false, density: false, fullScreen: false }}
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            onClick={() => {
              setMetricItem(undefined);
              setCreateModalVisible(true);
            }}
          >
            创建指标
          </Button>,
          <BatchCtrlDropDownButton
            key="ctrlBtnList"
            downloadLoading={downloadLoading}
            onDeleteConfirm={() => {
              queryBatchUpdateStatus(selectedRowKeys, StatusEnum.DELETED);
            }}
            onMenuClick={onMenuClick}
            onDownloadDateRangeChange={(searchDateRange, pickerType) => {
              downloadMetricQuery(selectedRowKeys, searchDateRange, pickerType);
            }}
          />,
        ]}
      />
      {createModalVisible && (
        <MetricInfoCreateForm
          domainId={selectDomainId}
          modelId={Number(modelId)}
          createModalVisible={createModalVisible}
          metricItem={metricItem}
          onSubmit={() => {
            setCreateModalVisible(false);
            actionRef?.current?.reload();
            dispatch({
              type: 'domainManger/queryMetricList',
              payload: {
                modelId,
              },
            });
          }}
          onCancel={() => {
            setCreateModalVisible(false);
          }}
        />
      )}
    </>
  );
};
export default connect(({ domainManger }: { domainManger: StateType }) => ({
  domainManger,
}))(ClassMetricTable);

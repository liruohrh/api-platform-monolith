import { parseDateToTimestamp, parseToTimestamp } from '@/utils';
import { SortOrder } from 'antd/lib/table/interface';

export function mappingDateRange(dateRange: string[]): string {
  const [start, end] = dateRange;
  return `${parseDateToTimestamp(start)},${parseDateToTimestamp(end)}`;
}
export function mappingRange(params: Record<string, any>) {
  const params2 = { ...params };
  if (params2['ctime']) {
    const [start, end] = params2['ctime'];
    params2['ctimeRange'] = `${parseToTimestamp(start)},${parseToTimestamp(end)}`;
    delete params2['ctime'];
  }
  if (params2['utime']) {
    const [start, end] = params2['utime'];
    params2['utimeRange'] = `${parseToTimestamp(start)},${parseToTimestamp(end)}`;
    delete params2['utime'];
  }
  return params2;
}
export function mappingDateTimeRange(params: Record<string, any>) {
  const params2 = { ...params };
  if (params2['ctime']) {
    const [start, end] = params2['ctime'];
    params2['ctime'] = `${parseToTimestamp(start)},${parseToTimestamp(end)}`;
  }
  if (params2['utime']) {
    const [start, end] = params2['utime'];
    params2['utime'] = `${parseToTimestamp(start)},${parseToTimestamp(end)}`;
  }
  return params2;
}
export function mappingSort2(sort: Record<string, SortOrder>) {
  const mSort: Record<string, boolean> = {};
  for (const key in sort) {
    const value = sort[key];
    if (value === 'ascend') {
      mSort[key + 'S'] = true;
    } else if (value === 'descend') {
      mSort[key + 'S'] = false;
    }
  }
  return mSort;
}

export function mappingSort(sort: Record<string, SortOrder>) {
  const mSort: Record<string, boolean> = {};
  for (const key in sort) {
    if (sort[key] === 'ascend') {
      mSort[key] = true;
    } else if (sort[key] === 'descend') {
      mSort[key] = false;
    }
    if (['ctime', 'utime'].includes(key)) {
      const v = mSort[key];
      delete mSort[key];
      mSort[`${key}S`] = v;
    }
  }
  return mSort;
}

import CryptoJS from 'crypto-js';
import dayjs from 'dayjs';

export function toggleSortValue(value?: boolean): boolean | undefined {
  let newValue: boolean | undefined;
  if (value === undefined) {
    newValue = true;
  } else if (value === true) {
    newValue = false;
  } else {
    newValue = undefined;
  }
  return newValue;
}

export function rangeToOrderNumber(value: string): {
  min?: number;
  minEq?: boolean;
  max?: number;
  maxEq?: boolean;
  value?: number;
} {
  const pv = value.trim();
  let result = pv.match(/^\[\s?(\d+)\s?,\s?(\d+)\s?\]$/);
  if (result && result.length === 3) {
    return {
      min: parseInt(result[1]),
      minEq: true,
      max: parseInt(result[2]),
      maxEq: true,
    };
  }
  result = pv.match(/^\(\s?(\d+)\s?,\s?(\d+)\s?\)$/);
  if (result && result.length === 3) {
    return {
      min: parseInt(result[1]),
      minEq: false,
      max: parseInt(result[2]),
      maxEq: false,
    };
  }
  result = pv.match(/^\[\s?(\d+)\s?,\s?(\d+)\s?\)$/);
  if (result && result.length === 3) {
    return {
      min: parseInt(result[1]),
      minEq: true,
      max: parseInt(result[2]),
      maxEq: false,
    };
  }
  result = pv.match(/^\(\s?(\d+)\s?,\s?(\d+)\s?\]$/);
  if (result && result.length === 3) {
    return {
      min: parseInt(result[1]),
      minEq: false,
      max: parseInt(result[2]),
      maxEq: true,
    };
  }

  //1个参数
  result = pv.match(/^\[\s?(\d+)\s?,\s?\)$/);
  if (result && result.length === 2) {
    return {
      min: parseInt(result[1]),
      minEq: true,
    };
  }
  result = pv.match(/^\(\s?(\d+)\s?,\s?\)$/);
  if (result && result.length === 2) {
    return {
      min: parseInt(result[1]),
      minEq: false,
    };
  }
  result = pv.match(/^\(\s?,\s?(\d+)\s?\]$/);
  if (result && result.length === 2) {
    return {
      max: parseInt(result[1]),
      maxEq: true,
    };
  }
  result = pv.match(/^\(\s?,\s?(\d+)\s?\)$/);
  if (result && result.length === 2) {
    return {
      max: parseInt(result[1]),
      maxEq: false,
    };
  }
  return {
    value: parseInt(pv),
  };
}
export function parseDateToTimestamp(timeStr: string) {
  const parsedTime = dayjs(timeStr, 'YYYY-MM-DD', true);
  if (!parsedTime.isValid()) {
    throw new Error(`非法的时间字符串: ${timeStr}. 允许格式: YYYY-MM-DD, 例如 2025-01-01`);
  }
  // 返回 Unix 时间戳（单位：秒）
  return Math.floor(parsedTime.unix()) * 1000;
}
export function parseToTimestamp(timeStr: string) {
  const parsedTime = dayjs(timeStr, 'YYYY-MM-DD HH:mm:ss', true);
  if (!parsedTime.isValid()) {
    throw new Error(
      `非法的时间字符串: ${timeStr}. 允许格式: YYYY-MM-DD HH:mm:ss, 例如 2025-01-01 00:00:00`,
    );
  }
  // 返回 Unix 时间戳（单位：秒）
  return Math.floor(parsedTime.unix()) * 1000;
}
export function formatTimestamp(mills?: number) {
  if (mills === undefined) {
    return mills;
  }
  return dayjs(mills).format('YYYY-MM-DD HH:mm:ss');
}

export function base64ToUtf8(base64Str: string) {
  return CryptoJS.enc.Base64.parse(base64Str).toString(CryptoJS.enc.Utf8);
}
export function emptyValueToNull(value: any): any {
  const newVal: any = {};
  Object.keys(value).forEach((key: string) => {
    if (!value[key] && value[key] !== false && value[key] !== 0) {
      return;
    }
    newVal[key] = value[key];
  });
  return newVal;
}
export function jsonPrettify(json?: string, space?: number): string {
  try {
    return JSON.stringify(JSON.parse(json || ''), null, space ? space : 4);
  } catch {
    return json || '';
  }
}
export function changeSortValue(sort: any): any {
  const newSort: any = {};
  Object.keys(sort).forEach((key) => {
    // @ts-ignore
    if (sort[key] === 'ascend') {
      newSort[key] = 'ASC';
    } else {
      newSort[key] = 'DESC';
    }
  });
  return newSort;
}

/**
 *
 * @param o1
 * @param o2
 * @return 返回o1和o2同属性值不同时o1的值
 */
export function getSamePropButValueNotEquals(o1: any, o2: any): any {
  const samePropButValueNotEquals = {};
  Object.keys(o1).forEach((key) => {
    // @ts-ignore
    if (o1[key] !== o2[key]) {
      // @ts-ignore
      samePropButValueNotEquals[key] = o1[key];
    }
  });
  return samePropButValueNotEquals;
}

export function localSort(data: any[], sort: any): any[] {
  for (const sortKey of Object.keys(sort)) {
    data?.sort((o1, o2) => {
      // @ts-ignore
      if (typeof o1[sortKey] === 'string') {
        // @ts-ignore
        return o1[sortKey].localeCompare(o2[sortKey]) * (sort[sortKey] === 'ascend' ? 1 : -1);
      } else {
        // @ts-ignore
        return (o1[sortKey] - o2[sortKey]) * (sort[sortKey] === 'ascend' ? 1 : -1);
      }
    });
  }
  return data;
}

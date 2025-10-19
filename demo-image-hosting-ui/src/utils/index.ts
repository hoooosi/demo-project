import { PermissionMask } from '@/const'
import SparkMD5 from "spark-md5";

export function getPermissionMask(permissions: bigint[]): string {
  let permission = BigInt(0)
  permissions.forEach((p) => {
    permission |= BigInt(p)
  })
  return permission.toString()
}

type PermissionKey = keyof typeof PermissionMask
export function getPermissionList(mask: bigint | undefined): bigint[] {
  const list: bigint[] = []
  if (!mask) return list
  let allMask = 0n;
  for (const key in PermissionMask) {
    if (key == "LONG_MAX")
      continue
    const value = PermissionMask[key as keyof typeof PermissionMask]
    if (typeof value === 'bigint')
      allMask |= value
  }

  mask &= allMask
  const maskStr = mask.toString(2).split('').reverse().join('')
  for (let i = 0; i < maskStr.length; ++i) {
    if (maskStr.charAt(i) == '1') list.push(1n << BigInt(i))
  }
  return list
}

export function hasPermission(mask: bigint, permission: bigint): boolean {
  return (mask & permission) == permission
}


export function getPermissionName(value: bigint): PermissionKey | undefined {
  for (const key of Object.keys(PermissionMask) as PermissionKey[]) {
    if (PermissionMask[key] === value) {
      return key
    }
  }
  return undefined
}

export function getStoragePercent(space: VO.SpaceVO): number {
  if (!space.maxSize || space.maxSize === '0') return 0
  return Math.min((Number(space.totalSize || 0) / Number(space.maxSize)) * 100, 100)
}

export function getCountPercent(space: VO.SpaceVO): number {
  // 新API不再直接提供count信息，需要通过其他方式获取
  // 临时返回0，后续需要根据实际需求实现
  return 0
}

export function formatSize(bytes: string): string {
  const _bytes = Number(bytes || 0)
  if (_bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(_bytes) / Math.log(k))
  return parseFloat((_bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function formatDate(dateStr: string): string {
  if (!dateStr) return 'Unknown'
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

export function conversionPage<T>(page: Page<T>): Page<T> {
  return {
    total: Number(page.total),
    size: Number(page.size),
    current: Number(page.current),
    pages: Number(page.pages),
    records: page.records,
  }
}

export function conversionQueryParams<T>(page: Page<T>): PageReq {
  return {
    size: Number(page.size),
    current: Number(page.current),
  }
}

export function wait(ms: number) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms)
  })
}


export function calculateFileMD5(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const blobSlice = File.prototype.slice;
    const chunkSize = 2097152;
    const chunks = Math.ceil(file.size / chunkSize);
    let currentChunk = 0;
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();

    fileReader.onload = (e) => {
      spark.append(e.target?.result as ArrayBuffer);
      currentChunk++;

      if (currentChunk < chunks) {
        loadNext();
      } else {
        const md5 = spark.end();
        resolve(md5);
      }
    };

    fileReader.onerror = () => {
      reject(new Error("Failed to read file"));
    };

    const loadNext = () => {
      const start = currentChunk * chunkSize;
      const end =
        start + chunkSize >= file.size ? file.size : start + chunkSize;
      fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    };

    loadNext();
  });
};
declare type BaseRes<T> = {
  code: number
  message: string
  data: T
}

declare type PageReq = {
  current: number;
  size: number;
  asc?: boolean;
}

declare type Page<T> = {
  records: T[]
  total: number;
  size: number;
  current: number;
  pages: number;
}

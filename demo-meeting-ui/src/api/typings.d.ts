declare type Resp<T> = {
  code: number
  message: string
  data: T
}

declare type Page<T> = {
  records: T[]
  total: number;
  size: number;
  current: number;
  pages: number;
}

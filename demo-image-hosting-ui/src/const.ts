export const PermissionMask = {
  // SPACE
  SPACE_VIEW: 1n << 0n,
  SPACE_SEARCH: 1n << 1n,
  SPACE_JOIN: 1n << 2n,
  SPACE_AUTO_APPROVE: 1n << 3n,
  SPACE_MANGE: 1n << 4n,

  // IMAGE 
  IMAGE_VIEW: 1n << 21n,
  IMAGE_UPLOAD: 1n << 22n,
  IMAGE_EDIT: 1n << 23n,
  IMAGE_DELETE: 1n << 24n,
  LONG_MAX: (1n << 63n) - 1n,
}

export const page = () => {
  return {
    records: [],
    total: 0,
    size: 0,
    current: 0,
    pages: 0,
  }
}

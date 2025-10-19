declare namespace VO {

  type UserVO = {
    id: string
    account: string
    name: string
    avatar: string
    profile: string
    role: 'USER' | 'ADMIN'
    createTime: string
    updateTime: string
  }

  type SpaceVO = {
    id: string
    name: string
    publicPermissionMask: string
    memberPermissionMask: string
    maxSize: string
    totalSize: string
    createTime: string
    updateTime: string
    editTime: string
  }

  type ImageVO = {
    id: string
    firstItemId: string
    spaceId: string
    userId: string
    name: string
    introduction: string
    tags: string[]
    createTime: string
    updateTime: string
    items: {
      id: string
      idxId: string
      fileId: string
      spaceId: string
      status: 'PROCESSING' | 'SUCCESS' | 'FAILED'
      md5: string
      contentType: string
      size: string
      width: number
      height: number
      createTime: string
      updateTime: string
    }[]
    creatorId: string
    creatorName: string
    creatorAvatar: string
  }

  type ApplicationVO = {
    id: string
    spaceId: string
    userId: string
    status: 'PENDING' | 'APPROVED' | 'REJECTED'
    handleType: 'AUTO' | 'MANUAL'
    handleTime: string
    createTime: string
    updateTime: string
    userName: string
    spaceName: string
    userAvatar: string
  }

  type MemberVo = {
    id: string
    sid: string
    uid: string
    permissionMask: string
    createTime: string
    updateTime: string
    editTime: string
    name: string
    account: string
    avatar: string
  }
}

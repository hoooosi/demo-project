/**
 * USER
 */
declare namespace Req {
  type LoginReq = {
    account: string
    password: string
  }

  type RegisterReq = {
    name: string
    account: string
    password: string
  }
}

/**
 * SPACE
 */
declare namespace Req {
  type EditSpaceReq = {
    spaceId: string
    name: string
    maxSize: string
    maxCount: string
    publicPermissionMask: string
    memberPermissionMask: string
  }

  type AddSpaceReq = {
    name: string
    maxSize: string
    publicPermissionMask: string
    memberPermissionMask: string
  }

  type QuerySpaceReq = PageReq & {
    name?: string
    mask?: string
  }
}

/**
 * PICTURE
 */
declare namespace Req {
  type EditImageReq = {
    idxId: string
    name: string
    introduction: string
    tags: string[]
  }

  type QueryImgReq = PageReq & {
    keyword?: string
    tags?: string[]
  }
}

/**
 * APPLY
 */
declare namespace Req {
  type HandleApplicationReq = {
    applicationId: string
    status: 'APPROVED' | 'REJECTED'
  }
}

/**
 * MEMBER
 */
declare namespace Req {
  type EditMemberReq = {
    memberId: string
    mask: string
  }
}

type PagingRequest = {
  pageNum: number
  pageSize: number
}


/**
 * USER
 */
declare namespace DTO {
  type LoginDTO = {
    email: string
    password: string
  }

  type RegisterDTO = {
    checkCode: string
    checkCodeKey: string
    email: string
    password: string
    nickName: string
  }
}


/**
 * MEETING
 */
declare namespace DTO {

  type LoadMeetingDTO = PagingRequest & {
    paging: DTO.PagingRequest
    keyword: string
  }

  type QuickStartMeetingDTO = {
    password: string
    meetingName: string
  }

  type PreJoinMeetingDTO = {
    meetingId: number
    password: string
  }

  type JoinMeetingDTO = {
    meetingId: number
    password: string
    nickName: string
  }
}
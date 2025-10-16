/**
 * USER
 */
declare namespace VO {
    type CheckCodeVO = {
        key: string
        base64: string
    }

    type UserVO = {
        userId: string
        email: string
        nickName: string
        sex: number
        status: number
        personalMeetingNo: number
        lastLoginTime: number
        lastOffTime: number
    }

    type MeetingVO = {
        meetingId: string
        meetingName: string
        meetingNo: string
        startTime: number
        endTime: number
        status: number
        participants: number
    }
}

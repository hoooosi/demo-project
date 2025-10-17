package io.github.hoooosi.meeting.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.meeting.common.constants.MeetingStatus;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.common.model.entity.Meeting;
import io.github.hoooosi.meeting.common.model.entity.MeetingMember;
import io.github.hoooosi.meeting.common.model.entity.User;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.common.utils.ThrowUtils;
import io.github.hoooosi.meeting.mapper.MeetingMapper;
import io.github.hoooosi.meeting.mapper.MeetingMemberMapper;
import io.github.hoooosi.meeting.mapper.UserMapper;
import io.github.hoooosi.meeting.service.MeetingService;
import io.github.hoooosi.meeting.websocket.ChannelContextUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements MeetingService {

    private final MeetingMemberMapper meetingMemberMapper;
    private final UserMapper userMapper;
    private final MeetingMapper meetingMapper;
    private final ChannelContextUtils channelContextUtils;
    private final RedisUtils redisUtils;

    @Transactional
    @Override
    public void quickStartMeeting(Long userId) {
        User user = userMapper.selectById(userId);

        if (this.lambdaQuery()
                .eq(Meeting::getMeetingNo, user.getPersonalMeetingNo())
                .eq(Meeting::getStatus, MeetingStatus.MEETING_STATUS_ONGOING)
                .exists()) {
            return;
        }

        Meeting meeting = new Meeting()
                .setMeetingNo(user.getPersonalMeetingNo())
                .setStartTime(System.currentTimeMillis())
                .setEndTime(System.currentTimeMillis() + 30 * 60 * 1000)
                .setCreator(userId)
                .setJoinType(1)
                .setStatus(MeetingStatus.MEETING_STATUS_ONGOING)
                .setMeetingName(user.getUserId() + "的会议");
        ThrowUtils.throwIfZero(meetingMapper.insert(meeting), ErrorCode.DATA_SAVE_ERROR);

        MeetingMember meetingMember = new MeetingMember()
                .setMeetingId(meeting.getMeetingId())
                .setUserId(userId)
                .setRole(1)
                .setNickName(user.getNickName())
                .setStatus(1);

        ThrowUtils.throwIfZero(meetingMemberMapper.insert(meetingMember), ErrorCode.DATA_SAVE_ERROR);
    }

    @Override
    public void preJoinMeeting(Long meetingNO, Long userId, String password) {
        Meeting meeting = meetingMapper.selectOne(Wrappers.lambdaQuery(Meeting.class)
                .eq(Meeting::getMeetingNo, meetingNO)
                .ne(Meeting::getStatus, MeetingStatus.MEETING_STATUS_ENDED));
        ThrowUtils.throwIfNull(meeting, ErrorCode.MEETING_NOT_FOUND);

        MeetingMember meetingMember = meetingMemberMapper.selectOne(Wrappers.lambdaQuery(MeetingMember.class)
                .eq(MeetingMember::getMeetingId, meeting.getMeetingId())
                .eq(MeetingMember::getUserId, userId));
        if (meetingMember != null)
            ThrowUtils.throwIf(meetingMember.getStatus() == 0, ErrorCode.BLACKLISTED);

        String meetingPassword = meeting.getJoinPass();
        ThrowUtils.throwIfNull(meeting, ErrorCode.MEETING_NOT_FOUND);
        ThrowUtils.throwIf(meetingPassword != null && !meetingPassword.equals(password), ErrorCode.MEETING_PASSWORD_ERROR);
    }

    @Override
    public List<Long> joinMeeting(Long meetingNO, Long userId, String password, String nickName) {
        this.preJoinMeeting(meetingNO, userId, password);
        channelContextUtils.addMeetingRoom(meetingNO, userId);
        return channelContextUtils.getMeetingRoomUserIds(meetingNO);
    }

    @Override
    public void leaveMeeting(Long meetingId, Long userId) {
        channelContextUtils.leaveRoom(meetingId, userId);
    }
}

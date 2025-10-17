package io.github.hoooosi.meeting.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.meeting.common.constants.MeetingStatus;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.common.model.entity.Meeting;
import io.github.hoooosi.meeting.common.model.entity.MeetingMember;
import io.github.hoooosi.meeting.common.model.entity.User;
import io.github.hoooosi.meeting.common.utils.ThrowUtils;
import io.github.hoooosi.meeting.mapper.MeetingMapper;
import io.github.hoooosi.meeting.mapper.MeetingMemberMapper;
import io.github.hoooosi.meeting.mapper.UserMapper;
import io.github.hoooosi.meeting.service.MeetingMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MeetingMemberServiceImpl extends ServiceImpl<MeetingMemberMapper, MeetingMember> implements MeetingMemberService {


}


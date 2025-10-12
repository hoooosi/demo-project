package io.github.hoooosi.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.meeting.common.model.entity.MeetingMember;
import io.github.hoooosi.meeting.mapper.MeetingMemberMapper;
import io.github.hoooosi.meeting.service.MeetingMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MeetingMemberServiceImpl extends ServiceImpl<MeetingMemberMapper, MeetingMember> implements MeetingMemberService {

    private final MeetingMemberMapper meetingMemberMapper;
}


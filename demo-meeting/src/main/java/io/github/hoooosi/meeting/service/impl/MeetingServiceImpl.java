package io.github.hoooosi.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.meeting.common.model.dto.MeetingDTO;
import io.github.hoooosi.meeting.common.model.entity.Meeting;
import io.github.hoooosi.meeting.common.model.entity.MeetingMember;
import io.github.hoooosi.meeting.common.model.vo.MeetingVO;
import io.github.hoooosi.meeting.common.model.vo.PageVO;
import io.github.hoooosi.meeting.mapper.MeetingMapper;
import io.github.hoooosi.meeting.mapper.MeetingMemberMapper;
import io.github.hoooosi.meeting.service.MeetingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements MeetingService {

    private final MeetingMapper meetingMapper;
    private final MeetingMemberMapper meetingMemberMapper;

}

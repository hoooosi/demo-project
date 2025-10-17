package io.github.hoooosi.meeting.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.hoooosi.meeting.common.model.entity.Meeting;
import io.github.hoooosi.meeting.common.model.entity.MeetingMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingMemberMapper extends BaseMapper<MeetingMember> {
}


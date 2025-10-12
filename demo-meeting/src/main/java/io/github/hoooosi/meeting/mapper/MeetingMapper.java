package io.github.hoooosi.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.hoooosi.meeting.common.model.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {
}


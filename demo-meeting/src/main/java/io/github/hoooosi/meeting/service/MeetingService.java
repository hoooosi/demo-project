package io.github.hoooosi.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.meeting.common.model.entity.Meeting;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MeetingService extends IService<Meeting> {

    void quickStartMeeting(Long userId);

    void preJoinMeeting(Long meetingId, Long userId, String password);

    List<Long> joinMeeting(Long meetingId, Long userId, String password, String nickName);

    void leaveMeeting(Long meetingId, Long userId);
}

package io.github.hoooosi.meeting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.meeting.common.annotation.CheckLogin;
import io.github.hoooosi.meeting.common.model.dto.MeetingDTO;
import io.github.hoooosi.meeting.common.model.vo.MeetingVO;
import io.github.hoooosi.meeting.common.model.vo.Resp;
import io.github.hoooosi.meeting.service.MeetingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting")
@AllArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/getCurrentMeeting")
    @CheckLogin
    public Resp<Void> getCurrentMeeting() {
        return Resp.success(null);
    }

    @GetMapping("/loadMeeting")
    @CheckLogin
    public Resp<Page<MeetingVO>> loadMeeting(MeetingDTO.LoadMeetingDTO dto) {
        return Resp.success(null);
    }

    @PostMapping("/quickMeeting")
    @CheckLogin
    public Resp<Void> quickMeeting() {
        return Resp.success(null);
    }

    @GetMapping("/joinMeeting")
    @CheckLogin
    public Resp<Void> joinMeeting() {
        return Resp.success(null);
    }
}

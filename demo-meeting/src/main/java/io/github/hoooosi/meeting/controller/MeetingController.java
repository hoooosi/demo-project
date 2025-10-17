package io.github.hoooosi.meeting.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.meeting.common.annotation.CheckLogin;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.common.model.dto.MeetingDTO;
import io.github.hoooosi.meeting.common.model.dto.TokenDTO;
import io.github.hoooosi.meeting.common.model.vo.MeetingVO;
import io.github.hoooosi.meeting.common.model.vo.Resp;
import io.github.hoooosi.meeting.common.utils.ThrowUtils;
import io.github.hoooosi.meeting.common.utils.TokenUtils;
import io.github.hoooosi.meeting.service.MeetingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/meeting")
@AllArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final TokenUtils tokenUtils;

    @GetMapping("/getCurrentMeeting")
    @CheckLogin
    public Resp<Long> getCurrentMeeting() {
        TokenDTO tokenDTO = tokenUtils.getTokenDTO();
        return Resp.success(tokenDTO.getRoomId());
    }

    @GetMapping("/loadMeeting")
    @CheckLogin
    public Resp<Page<MeetingVO>> loadMeeting(MeetingDTO.LoadMeetingDTO dto) {
        return Resp.success(null);
    }

    @PostMapping("/quickMeeting")
    @CheckLogin
    public Resp<Void> quickMeeting(@RequestBody MeetingDTO.QuickStartMeetingDTO dto) {
        TokenDTO tokenDTO = tokenUtils.getTokenDTO();
        ThrowUtils.throwIf(tokenDTO.getRoomId() != null, ErrorCode.ALREADY_IN_MEETING);
        meetingService.quickStartMeeting(tokenDTO.getUserId());
        return Resp.success();
    }

    @PostMapping("/preJoinMeeting")
    @CheckLogin
    public Resp<Void> preJoinMeeting(@RequestBody MeetingDTO.PreJoinMeetingDTO dto) {
        meetingService.preJoinMeeting(dto.meetingId(), tokenUtils.getUserId(), dto.password());
        return Resp.success(null);
    }

    @PostMapping("/joinMeeting")
    @CheckLogin
    public Resp<List<Long>> joinMeeting(@RequestBody MeetingDTO.JoinMeetingDTO dto) {
        return Resp.success(meetingService.joinMeeting(dto.meetingId(), tokenUtils.getUserId(), dto.password(), dto.nickName()));
    }

    @GetMapping("/leaveMeeting")
    @CheckLogin
    public Resp<Void> leaveMeeting() {
        TokenDTO tokenDTO = tokenUtils.getTokenDTO();
        log.info("tokenDto: {}",tokenDTO);
        meetingService.leaveMeeting(tokenDTO.getRoomId(), tokenDTO.getUserId());
        return Resp.success();
    }
}

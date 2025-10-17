package io.github.hoooosi.meeting.controller;

import io.github.hoooosi.meeting.common.model.vo.Resp;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/message")
public class MessageController {
    @GetMapping("/getHistoryMessage")
    public Resp<Void> getHistoryMessage() {
        return Resp.success(null);
    }
}

package io.github.hoooosi.meeting.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MessageSendDTO<T> implements Serializable {
    private Integer messageSendType;
    private String meetingId;
    private Long receiverId;
    private Integer messageType;
    private String senderId;
    private String senderNickName;
    private T content;
    private Long sendTime;
}

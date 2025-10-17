package io.github.hoooosi.meeting.websocket.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MessageSendDTO<T> implements Serializable {
    private int messageTargetType;
    private int messageType;
    private String receiverId;

    private String senderId;
    private T content;
    private String sendTime;
}

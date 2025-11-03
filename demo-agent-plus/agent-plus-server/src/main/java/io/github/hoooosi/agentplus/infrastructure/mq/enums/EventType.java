package io.github.hoooosi.agentplus.infrastructure.mq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {

    DOC_SYNC_RAG(4000, "文件入库"), DOC_REFRESH_ORG(4001, "文件ocr");

    private final Integer code;
    private final String desc;


    /** 按key获取枚举 */
    public static EventType getEnum(Integer code) {
        for (EventType e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}

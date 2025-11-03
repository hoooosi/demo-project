package io.github.hoooosi.agentplus.domain.rag.strategy;


import io.github.hoooosi.agentplus.domain.rag.message.RagDocMessage;

public interface DocumentProcessingStrategy {

    /** 处理
     * @param ragDocSyncOcrMessage mq消息
     * @param strategy 策略 */
    void handle(RagDocMessage ragDocSyncOcrMessage, String strategy) throws Exception;

}

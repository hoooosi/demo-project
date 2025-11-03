package io.github.hoooosi.agentplus.domain.rag.consumer;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.hoooosi.agentplus.domain.rag.message.RagDocSyncStorageMessage;
import io.github.hoooosi.agentplus.domain.rag.model.DocumentUnitEntity;
import io.github.hoooosi.agentplus.domain.rag.repository.DocumentUnitRepository;
import io.github.hoooosi.agentplus.domain.rag.service.EmbeddingDomainService;
import io.github.hoooosi.agentplus.domain.rag.service.FileDetailDomainService;
import io.github.hoooosi.agentplus.infrastructure.mq.core.MessageEnvelope;
import io.github.hoooosi.agentplus.infrastructure.mq.core.MessageHeaders;
import io.github.hoooosi.agentplus.infrastructure.mq.events.RagDocSyncStorageEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * RAG文档向量化存储消费者 (RocketMQ)
 * 
 * @author shilong.zang
 * @date 20:51
 */
@Component
@RocketMQMessageListener(topic = RagDocSyncStorageEvent.EXCHANGE_NAME, consumerGroup = "rag-doc-storage-consumer-group", selectorExpression = RagDocSyncStorageEvent.ROUTE_KEY)
public class RagDocStorageConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(RagDocStorageConsumer.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final EmbeddingDomainService embeddingService;
    private final FileDetailDomainService fileDetailDomainService;
    private final DocumentUnitRepository documentUnitRepository;

    public RagDocStorageConsumer(EmbeddingDomainService embeddingService,
            FileDetailDomainService fileDetailDomainService,
            DocumentUnitRepository documentUnitRepository) {
        this.embeddingService = embeddingService;
        this.fileDetailDomainService = fileDetailDomainService;
        this.documentUnitRepository = documentUnitRepository;
    }

    @Override
    public void onMessage(String messageBody) {
        try {
            // 解析消息体为 MessageEnvelope
            MessageEnvelope<RagDocSyncStorageMessage> envelope = OBJECT_MAPPER.readValue(
                    messageBody,
                    OBJECT_MAPPER.getTypeFactory().constructParametricType(
                            MessageEnvelope.class,
                            RagDocSyncStorageMessage.class));

            MDC.put(MessageHeaders.TRACE_ID,
                    Objects.nonNull(envelope.getTraceId()) ? envelope.getTraceId() : IdWorker.getTimeId());
            RagDocSyncStorageMessage mqRecordReqDTO = envelope.getData();

            log.info("当前文件 {} 页面 {} ———— 开始向量化", mqRecordReqDTO.getFileName(), mqRecordReqDTO.getPage());

            // 执行向量化处理
            embeddingService.syncStorage(mqRecordReqDTO);

            // 更新向量化进度（假设每个页面向量化完成后更新）
            updateEmbeddingProgress(mqRecordReqDTO);

            log.info("当前文件 {} 第{}页 ———— 向量化完成", mqRecordReqDTO.getFileName(), mqRecordReqDTO.getPage());
        } catch (Exception e) {
            log.error("向量化过程中发生异常", e);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 更新向量化进度
     * 
     * @param message 向量化消息
     */
    private void updateEmbeddingProgress(RagDocSyncStorageMessage message) {
        try {
            String fileId = message.getFileId();

            // 获取文件总页数来计算进度
            var fileEntity = fileDetailDomainService.getFileByIdWithoutUserCheck(fileId);
            Integer totalPages = fileEntity.getFilePageSize();

            if (totalPages != null && totalPages > 0) {
                // 查询已完成向量化的页面数量
                long completedVectorPages = documentUnitRepository.selectCount(
                        Wrappers.<DocumentUnitEntity>lambdaQuery()
                                .eq(DocumentUnitEntity::getFileId, fileId)
                                .eq(DocumentUnitEntity::getIsVector, true));

                // 当前页面完成后的总完成页数
                int currentCompletedPages = (int) (completedVectorPages + 1);

                // 计算百分比：已完成的页数 / 总页数 * 100
                double progress = ((double) currentCompletedPages / totalPages) * 100.0;

                // 使用非deprecated的方法更新进度
                log.debug("更新文件{}的嵌入进度: {}/{} ({}%)", fileId, currentCompletedPages, totalPages,
                        String.format("%.1f", progress));

                // 检查是否所有页面都已完成向量化
                if (currentCompletedPages >= totalPages) {
                    // 通过状态机完成向量化处理
                    fileDetailDomainService.completeFileEmbeddingProcessing(fileId, fileEntity.getUserId());
                    log.info("文件{}的所有页面均已向量化，标记为完成", fileId);
                }
            }
        } catch (Exception e) {
            log.warn("更新文件{}的嵌入进度失败: {}", message.getFileId(), e.getMessage());
        }
    }
}

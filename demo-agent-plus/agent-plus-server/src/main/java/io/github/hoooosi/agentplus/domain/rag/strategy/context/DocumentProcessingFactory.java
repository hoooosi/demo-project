package io.github.hoooosi.agentplus.domain.rag.strategy.context;

import io.github.hoooosi.agentplus.domain.rag.model.enums.DocumentProcessingType;
import io.github.hoooosi.agentplus.domain.rag.strategy.DocumentProcessingStrategy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Map;


@Service
public class DocumentProcessingFactory {

    @Resource
    private Map<String, DocumentProcessingStrategy> documentProcessingStrategyMap;

    public DocumentProcessingStrategy getDocumentStrategyHandler(String strategy) {
        return documentProcessingStrategyMap.get(DocumentProcessingType.getLabelByValue(strategy));
    }
}

package io.github.hoooosi.agentplus.infrastructure.rag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 嵌入式配置 */
@Configuration
@EnableConfigurationProperties(EmbeddingProperties.class)
@AllArgsConstructor
public class EmbeddingConfig {

    private final EmbeddingProperties embeddingProperties;

    /** 向量化存储配置
     * @return PgVectorEmbeddingStore实例 */
    @Bean
    public EmbeddingStore<TextSegment> initEmbeddingStore() {
        EmbeddingProperties.VectorStore vectorStoreConfig = embeddingProperties.getVectorStore();

        return PgVectorEmbeddingStore.builder().table(vectorStoreConfig.getTable())
                .dropTableFirst(vectorStoreConfig.isDropTableFirst()).createTable(vectorStoreConfig.isCreateTable())
                .host(vectorStoreConfig.getHost()).port(vectorStoreConfig.getPort()).user(vectorStoreConfig.getUser())
                .password(vectorStoreConfig.getPassword()).dimension(vectorStoreConfig.getDimension())
                .database(vectorStoreConfig.getDatabase()).build();
    }

}

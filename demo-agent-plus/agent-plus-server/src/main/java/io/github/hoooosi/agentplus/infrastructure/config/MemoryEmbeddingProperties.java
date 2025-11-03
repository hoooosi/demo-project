package io.github.hoooosi.agentplus.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Memory 向量存储配置属性（与 embedding.vector-store 结构一致，独立命名空间） */
@ConfigurationProperties(prefix = "memory.embedding")
@Data
public class MemoryEmbeddingProperties {

    private String name;
    private String apiKey;
    private String apiUrl;
    private String model;
    private int timeout;
    private VectorStore vectorStore = new VectorStore();

    @Data
    public static class VectorStore {
        private String host;
        private int port = 5432;
        private String user;
        private String password;
        private String database;
        private String table = "public.memory_vector_store";
        private int dimension = 1024;
        private boolean dropTableFirst = false;
        private boolean createTable = true;
    }
}

package io.github.hoooosi.agentplus.infrastructure.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** OpenAI嵌入服务配置属性类 用于绑定application.yml中的embedding配置 */
@ConfigurationProperties(prefix = "embedding")
@Data
public class EmbeddingProperties {
    /** 嵌入服务名称 */
    private String name;
    /** API密钥 */
    private String apiKey;
    /** API URL */
    private String apiUrl;
    /** 使用的模型名称 */
    private String model;
    /** 请求超时时间(毫秒) */
    private int timeout;
    /** 向量存储配置 */
    private VectorStore vectorStore = new VectorStore();

    /** 向量存储配置内部类 */
    @Data
    public static class VectorStore {
        /** 数据库主机地址 */
        private String host;
        /** 数据库端口 */
        private int port = 5432;
        /** 数据库用户名 */
        private String user;
        /** 数据库密码 */
        private String password;
        /** 数据库名 */
        private String database;
        /** 向量表名 */
        private String table = "public.vector_store";
        /** 向量维度 */
        private int dimension = 1024;
        /** 是否先删除表 */
        private boolean dropTableFirst = false;
        /** 是否创建表 */
        private boolean createTable = false;
    }
}

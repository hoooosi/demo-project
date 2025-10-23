package io.github.hoooosi.demo.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@Configuration
@AllArgsConstructor
public class VectorStoreInit {

    private final VectorStore vectorStore;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("knowledge/sample-knowledge.txt");
            if (resource.exists()) {
                // 读取文档
                TextReader textReader = new TextReader(resource);
                List<Document> documents = textReader.get();

                // 分割文档
                TokenTextSplitter splitter = new TokenTextSplitter();
                List<Document> splitDocuments = splitter.apply(documents);

                // 存储到向量数据库
                vectorStore.add(splitDocuments);
                System.out.println("✓ 知识库初始化成功！");
            }
        } catch (Exception e) {
            System.err.println("知识库初始化失败: " + e.getMessage());
        }
    }
}

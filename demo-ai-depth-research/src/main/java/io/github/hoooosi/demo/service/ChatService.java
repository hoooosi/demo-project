package io.github.hoooosi.demo.service;

import lombok.Data;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public RagResp chatWithContext(String question) {
        // 获取相关文档
        List<Document> relevantDocs = vectorStore
                .similaritySearch(SearchRequest
                        .builder()
                        .query(question)
                        .topK(3).build());

        // 构建上下文
        String context = relevantDocs.stream()
                .map(doc -> doc.getText())
                .collect(Collectors.joining("\n\n"));

        // 生成回答
        String systemPrompt = """
                你是一个专业的问答助手。请严格基于提供的 "上下文信息" 来回答用户的问题。
                如果上下文信息中不包含足够的信息来回答问题，请礼貌地说明你找不到答案，不要凭空捏造。
                
                上下文信息如下：
                ---
                %s
                ---
                """.formatted(context);
        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();

        return new RagResp(answer, relevantDocs);
    }

    public record RagResp(String answer, List<Document> relevantDocs) {
    }
}

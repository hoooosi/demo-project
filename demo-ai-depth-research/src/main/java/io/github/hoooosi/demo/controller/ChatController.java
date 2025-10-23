package io.github.hoooosi.demo.controller;

import io.github.hoooosi.demo.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/ask")
    public String ask(String message) {
        return chatService.chat(message);
    }

    @GetMapping("/rag/ask")
    public ChatService.RagResp rag(String message) {
        return chatService.chatWithContext(message);
    }
}

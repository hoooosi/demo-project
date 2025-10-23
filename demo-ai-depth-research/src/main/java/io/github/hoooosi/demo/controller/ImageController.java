package io.github.hoooosi.demo.controller;

import io.github.hoooosi.demo.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
@AllArgsConstructor
public class ImageController {
    private final ImageService imageGenerationService;

    @GetMapping("/draw")
    public String draw(@RequestParam("prompt") String prompt) {
        return imageGenerationService.generateImage(prompt);
    }
}

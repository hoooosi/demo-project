package io.github.hoooosi.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 图像生成服务
 */
@Slf4j
@Service
public class ImageService {

    private final ImageModel imageModel;

    public ImageService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    /**
     * 根据文本描述生成图片
     *
     * @param prompt 文本描述
     * @return 图片 URL
     */
    public String generateImage(String prompt) {
        ImageGeneration result = imageModel.call(
                new ImagePrompt(prompt)).getResult();
        return result.getOutput().getUrl();
    }
}

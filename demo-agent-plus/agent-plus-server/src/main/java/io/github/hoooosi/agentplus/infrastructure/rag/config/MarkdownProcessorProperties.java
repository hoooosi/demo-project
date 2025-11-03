package io.github.hoooosi.agentplus.infrastructure.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Markdown处理器配置属性类 用于配置Markdown文档处理和段落拆分的相关参数 */
@Configuration
@ConfigurationProperties(prefix = "rag.markdown")
@Data
public class MarkdownProcessorProperties {

    /** 段落拆分配置 */
    private SegmentSplit segmentSplit = new SegmentSplit();

    /** 段落拆分配置内部类 */
    @Data
    public static class SegmentSplit {
        /** 最大段落字符数 */
        private int maxLength = 1800;
        /** 最小段落字符数 */
        private int minLength = 200;
        /** 安全缓冲区大小 */
        private int bufferSize = 100;
        /** 是否启用段落拆分 */
        private boolean enabled = true;
        /** 是否启用重叠分段 */
        private boolean enableOverlap = false;
        /** 重叠区域大小 */
        private int overlapSize = 50;
    }
}
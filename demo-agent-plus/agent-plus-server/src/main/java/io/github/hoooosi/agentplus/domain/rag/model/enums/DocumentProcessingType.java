package io.github.hoooosi.agentplus.domain.rag.model.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum DocumentProcessingType {

    PDF(Set.of("PDF"), "pdf"),
    DOCX(Set.of("DOC", "DOCX", "PPT", "PPTX", "XLS", "XLSX"), "word"),
    TXT(Set.of("TXT", "HTML"), "txt"),
    MARKDOWN(Set.of("MD", "MARKDOWN"), "markdown"),
    ;

    private final Set<String> value;
    private final String label;

    public static String getLabelByValue(String label) {
        for (DocumentProcessingType enumValue : DocumentProcessingType.values()) {
            if (enumValue.value.contains(label)) {
                return enumValue.label;
            }
        }
        return null;
    }

}

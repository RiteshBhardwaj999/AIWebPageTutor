package com.aiwebpagetutor.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AiRequest {

    @NotBlank(message = "Text is required")
    private String text;

    private String sourceUrl;
    private String pageTitle;
    private String targetLanguage; // for translation
}

package com.aiwebpagetutor.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiResponse {
    private String result;
    private String actionType;
    private Long historyId;
}

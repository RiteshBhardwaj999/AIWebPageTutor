package com.aiwebpagetutor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LearningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String actionType; // EXPLAIN, SUMMARIZE, QUIZ, FLASHCARD, TRANSLATE, DIAGRAM

    @Column(columnDefinition = "TEXT")
    private String sourceText;

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(columnDefinition = "TEXT")
    private String pageTitle;

    @Column(columnDefinition = "TEXT")
    private String aiResponse;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

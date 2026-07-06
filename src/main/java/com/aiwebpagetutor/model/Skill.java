package com.aiwebpagetutor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String category;

    @Builder.Default
    private Integer interactionCount = 0;

    @Builder.Default
    private LocalDateTime lastStudied = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

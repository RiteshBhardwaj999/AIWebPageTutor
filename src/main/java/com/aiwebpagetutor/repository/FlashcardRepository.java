package com.aiwebpagetutor.repository;

import com.aiwebpagetutor.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Flashcard> findByUserIdAndTopicIgnoreCaseOrderByCreatedAtDesc(Long userId, String topic);
}

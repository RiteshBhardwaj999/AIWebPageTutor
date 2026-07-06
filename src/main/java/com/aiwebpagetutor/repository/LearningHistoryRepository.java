package com.aiwebpagetutor.repository;

import com.aiwebpagetutor.model.LearningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LearningHistoryRepository extends JpaRepository<LearningHistory, Long> {
    List<LearningHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<LearningHistory> findByUserIdAndActionTypeOrderByCreatedAtDesc(Long userId, String actionType);
}

package com.aiwebpagetutor.service;

import com.aiwebpagetutor.model.LearningHistory;
import com.aiwebpagetutor.model.User;
import com.aiwebpagetutor.repository.LearningHistoryRepository;
import com.aiwebpagetutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final LearningHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public LearningHistory save(String email, String actionType, String sourceText,
                                 String sourceUrl, String pageTitle, String aiResponse) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LearningHistory history = LearningHistory.builder()
                .user(user)
                .actionType(actionType)
                .sourceText(sourceText)
                .sourceUrl(sourceUrl)
                .pageTitle(pageTitle)
                .aiResponse(aiResponse)
                .build();

        return historyRepository.save(history);
    }

    public List<LearningHistory> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return historyRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<LearningHistory> getHistoryByType(String email, String actionType) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return historyRepository.findByUserIdAndActionTypeOrderByCreatedAtDesc(user.getId(), actionType);
    }

    public void deleteHistory(String email, Long historyId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LearningHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History entry not found"));
        if (!history.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this entry");
        }
        historyRepository.delete(history);
    }
}

package com.aiwebpagetutor.service;

import com.aiwebpagetutor.model.Flashcard;
import com.aiwebpagetutor.model.LearningHistory;
import com.aiwebpagetutor.repository.FlashcardRepository;
import com.aiwebpagetutor.repository.LearningHistoryRepository;
import com.aiwebpagetutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final LearningHistoryRepository historyRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;

    public String exportHistoryToMarkdown(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<LearningHistory> history = historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        StringBuilder md = new StringBuilder("# Learning History\n\n");

        for (LearningHistory entry : history) {
            md.append("## ").append(entry.getActionType()).append("\n");
            md.append("**Date:** ").append(entry.getCreatedAt()).append("\n\n");
            if (entry.getPageTitle() != null) {
                md.append("**Page:** ").append(entry.getPageTitle()).append("\n\n");
            }
            if (entry.getSourceUrl() != null) {
                md.append("**URL:** ").append(entry.getSourceUrl()).append("\n\n");
            }
            md.append("### Selected Text\n").append(entry.getSourceText()).append("\n\n");
            md.append("### AI Response\n").append(entry.getAiResponse()).append("\n\n");
            md.append("---\n\n");
        }

        return md.toString();
    }

    public String exportFlashcardsToMarkdown(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<Flashcard> flashcards = flashcardRepository.findByUserIdOrderByCreatedAtDesc(userId);
        StringBuilder md = new StringBuilder("# Flashcards\n\n");

        for (int i = 0; i < flashcards.size(); i++) {
            Flashcard card = flashcards.get(i);
            md.append("## Card ").append(i + 1).append("\n");
            if (card.getTopic() != null) {
                md.append("**Topic:** ").append(card.getTopic()).append("\n\n");
            }
            md.append("**Q:** ").append(card.getQuestion()).append("\n\n");
            md.append("**A:** ").append(card.getAnswer()).append("\n\n");
            md.append("---\n\n");
        }

        return md.toString();
    }
}

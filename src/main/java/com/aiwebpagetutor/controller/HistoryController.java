package com.aiwebpagetutor.controller;

import com.aiwebpagetutor.model.LearningHistory;
import com.aiwebpagetutor.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<LearningHistory>> getHistory(Authentication auth) {
        return ResponseEntity.ok(historyService.getHistory(auth.getName()));
    }

    @GetMapping("/type/{actionType}")
    public ResponseEntity<List<LearningHistory>> getHistoryByType(
            @PathVariable String actionType, Authentication auth) {
        return ResponseEntity.ok(historyService.getHistoryByType(auth.getName(), actionType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id, Authentication auth) {
        historyService.deleteHistory(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}

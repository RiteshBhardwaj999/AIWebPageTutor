package com.aiwebpagetutor.controller;

import com.aiwebpagetutor.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/history/markdown")
    public ResponseEntity<byte[]> exportHistory(Authentication auth) {
        String markdown = exportService.exportHistoryToMarkdown(auth.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=learning-history.md")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(markdown.getBytes());
    }

    @GetMapping("/flashcards/markdown")
    public ResponseEntity<byte[]> exportFlashcards(Authentication auth) {
        String markdown = exportService.exportFlashcardsToMarkdown(auth.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=flashcards.md")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(markdown.getBytes());
    }
}

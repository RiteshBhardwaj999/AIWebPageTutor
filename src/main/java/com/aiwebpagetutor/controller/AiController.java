package com.aiwebpagetutor.controller;

import com.aiwebpagetutor.dto.request.AiRequest;
import com.aiwebpagetutor.dto.response.AiResponse;
import com.aiwebpagetutor.model.LearningHistory;
import com.aiwebpagetutor.service.GeminiService;
import com.aiwebpagetutor.service.HistoryService;
import com.aiwebpagetutor.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;
    private final HistoryService historyService;
    private final SkillService skillService;

    @PostMapping("/explain")
    public ResponseEntity<AiResponse> explain(@Valid @RequestBody AiRequest request,
                                               Authentication auth) {
        String result = geminiService.explain(request.getText());
        LearningHistory history = saveAndTrack(auth, "EXPLAIN", request, result);
        return ResponseEntity.ok(buildResponse(result, "EXPLAIN", history.getId()));
    }

    @PostMapping("/summarize")
    public ResponseEntity<AiResponse> summarize(@Valid @RequestBody AiRequest request,
                                                 Authentication auth) {
        String result = geminiService.summarize(request.getText());
        LearningHistory history = saveAndTrack(auth, "SUMMARIZE", request, result);
        return ResponseEntity.ok(buildResponse(result, "SUMMARIZE", history.getId()));
    }

    @PostMapping("/quiz")
    public ResponseEntity<AiResponse> quiz(@Valid @RequestBody AiRequest request,
                                            Authentication auth) {
        String result = geminiService.generateQuiz(request.getText());
        LearningHistory history = saveAndTrack(auth, "QUIZ", request, result);
        return ResponseEntity.ok(buildResponse(result, "QUIZ", history.getId()));
    }

    @PostMapping("/flashcards")
    public ResponseEntity<AiResponse> flashcards(@Valid @RequestBody AiRequest request,
                                                  Authentication auth) {
        String result = geminiService.generateFlashcards(request.getText());
        LearningHistory history = saveAndTrack(auth, "FLASHCARD", request, result);
        return ResponseEntity.ok(buildResponse(result, "FLASHCARD", history.getId()));
    }

    @PostMapping("/diagram")
    public ResponseEntity<AiResponse> diagram(@Valid @RequestBody AiRequest request,
                                               Authentication auth) {
        String result = geminiService.generateDiagram(request.getText());
        LearningHistory history = saveAndTrack(auth, "DIAGRAM", request, result);
        return ResponseEntity.ok(buildResponse(result, "DIAGRAM", history.getId()));
    }

    @PostMapping("/translate")
    public ResponseEntity<AiResponse> translate(@Valid @RequestBody AiRequest request,
                                                 Authentication auth) {
        String lang = request.getTargetLanguage() != null ? request.getTargetLanguage() : "English";
        String result = geminiService.translate(request.getText(), lang);
        LearningHistory history = saveAndTrack(auth, "TRANSLATE", request, result);
        return ResponseEntity.ok(buildResponse(result, "TRANSLATE", history.getId()));
    }

    @PostMapping("/examples")
    public ResponseEntity<AiResponse> examples(@Valid @RequestBody AiRequest request,
                                                Authentication auth) {
        String result = geminiService.getRealWorldExamples(request.getText());
        LearningHistory history = saveAndTrack(auth, "EXAMPLES", request, result);
        return ResponseEntity.ok(buildResponse(result, "EXAMPLES", history.getId()));
    }

    @PostMapping("/code-examples")
    public ResponseEntity<AiResponse> codeExamples(@Valid @RequestBody AiRequest request,
                                                    Authentication auth) {
        String result = geminiService.getCodeExamples(request.getText());
        LearningHistory history = saveAndTrack(auth, "CODE_EXAMPLES", request, result);
        return ResponseEntity.ok(buildResponse(result, "CODE_EXAMPLES", history.getId()));
    }

    @PostMapping("/resources")
    public ResponseEntity<AiResponse> resources(@Valid @RequestBody AiRequest request,
                                                 Authentication auth) {
        String result = geminiService.getRelatedResources(request.getText());
        LearningHistory history = saveAndTrack(auth, "RESOURCES", request, result);
        return ResponseEntity.ok(buildResponse(result, "RESOURCES", history.getId()));
    }

    private LearningHistory saveAndTrack(Authentication auth, String actionType,
                                          AiRequest request, String result) {
        String email = auth.getName();
        LearningHistory history = historyService.save(
                email, actionType, request.getText(),
                request.getSourceUrl(), request.getPageTitle(), result
        );
        // Auto-track skill from page title if available
        if (request.getPageTitle() != null) {
            skillService.trackSkill(email, request.getPageTitle(), actionType);
        }
        return history;
    }

    private AiResponse buildResponse(String result, String actionType, Long historyId) {
        return AiResponse.builder()
                .result(result)
                .actionType(actionType)
                .historyId(historyId)
                .build();
    }
}

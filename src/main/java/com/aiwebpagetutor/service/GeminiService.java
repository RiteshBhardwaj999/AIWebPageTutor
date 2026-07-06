package com.aiwebpagetutor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model:llama-3.3-70b-versatile}")
    private String model;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${ai.api.base-url:https://api.groq.com/openai/v1}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public String explain(String text) {
        String prompt = "Explain the following text in simpler terms. "
                + "Use clear language that a student would understand. "
                + "Include a real-world analogy if possible.\n\n" + text;
        return callAi(prompt);
    }

    public String summarize(String text) {
        String prompt = "Summarize the following text concisely. "
                + "Highlight the key points in bullet format.\n\n" + text;
        return callAi(prompt);
    }

    public String generateQuiz(String text) {
        String prompt = "Based on the following text, generate 5 multiple-choice quiz questions. "
                + "Format each question as:\n"
                + "Q1. [question]\n"
                + "  A) [option]\n  B) [option]\n  C) [option]\n  D) [option]\n"
                + "  Correct Answer: [letter]\n\n"
                + "Do NOT return JSON. Use plain readable text only.\n\n" + text;
        return callAi(prompt);
    }

    public String generateFlashcards(String text) {
        String prompt = "Based on the following text, generate 5 flashcards for studying. "
                + "Format each flashcard as:\n"
                + "Card 1:\n  Front: [question]\n  Back: [answer]\n\n"
                + "Do NOT return JSON. Use plain readable text only.\n\n" + text;
        return callAi(prompt);
    }

    public String generateDiagram(String text) {
        String prompt = "Create a simple text-based diagram that visually represents "
                + "the key concepts and relationships in the following text. "
                + "Use arrows (-->) and indentation to show relationships. "
                + "Keep it readable as plain text.\n\n" + text;
        return callAi(prompt);
    }

    public String translate(String text, String targetLanguage) {
        String prompt = "Translate the following text to " + targetLanguage
                + ". Provide the translation only.\n\n" + text;
        return callAi(prompt);
    }

    public String getRealWorldExamples(String text) {
        String prompt = "Give 3 real-world examples that illustrate the concepts in the following text. "
                + "Make them relatable and easy to understand.\n\n" + text;
        return callAi(prompt);
    }

    public String getCodeExamples(String text) {
        String prompt = "Provide practical code examples that demonstrate the concepts in the following text. "
                + "Include comments explaining each part. Use the most appropriate programming language.\n\n" + text;
        return callAi(prompt);
    }

    public String getRelatedResources(String text) {
        String prompt = "Suggest 5 related topics for further learning based on the following text. "
                + "Format each as:\n"
                + "1. [Topic] - [short description]\n   Search: [what to google]\n\n"
                + "Do NOT return JSON. Use plain readable text only.\n\n" + text;
        return callAi(prompt);
    }

    public String getRecommendations(List<String> studiedSkills) {
        String prompt = "Based on these skills a student has been studying: "
                + String.join(", ", studiedSkills)
                + "\n\nRecommend 5 topics they should learn next. "
                + "Format each as:\n"
                + "1. [Topic] (Difficulty: beginner/intermediate/advanced)\n   Why: [reason]\n\n"
                + "Do NOT return JSON. Use plain readable text only.";
        return callAi(prompt);
    }

    private String callAi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are an AI learning assistant that helps students understand content from webpages. "
                                + "IMPORTANT: Always respond in plain, human-readable text. "
                                + "NEVER use JSON, code blocks, or curly braces in your responses. "
                                + "Use numbered lists, bullet points, and clear formatting instead."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 2048
        );

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Map<?, ?> response = webClient.post()
                        .uri("/chat/completions")
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                return extractTextFromResponse(response);
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                boolean isRateLimit = msg.contains("429") || msg.contains("Too Many Requests")
                        || msg.contains("rate_limit");

                if (isRateLimit && attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return "Error: Request interrupted.";
                    }
                } else {
                    return "Error calling AI service: " + msg;
                }
            }
        }

        return "Error: AI service unavailable after retries. Please wait a moment and try again.";
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<?, ?> response) {
        if (response == null) {
            return "Error: No response from AI service.";
        }
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "Error parsing AI response: " + e.getMessage();
        }
    }
}

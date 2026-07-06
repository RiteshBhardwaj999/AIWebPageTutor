package com.aiwebpagetutor.controller;

import com.aiwebpagetutor.model.Skill;
import com.aiwebpagetutor.service.GeminiService;
import com.aiwebpagetutor.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final GeminiService geminiService;

    @GetMapping
    public ResponseEntity<List<Skill>> getSkills(Authentication auth) {
        return ResponseEntity.ok(skillService.getSkills(auth.getName()));
    }

    @PostMapping("/track")
    public ResponseEntity<Skill> trackSkill(@RequestBody Map<String, String> body,
                                             Authentication auth) {
        String name = body.get("name");
        String category = body.getOrDefault("category", "General");
        return ResponseEntity.ok(skillService.trackSkill(auth.getName(), name, category));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<String> getRecommendations(Authentication auth) {
        List<Skill> skills = skillService.getSkills(auth.getName());
        List<String> skillNames = skills.stream().map(Skill::getName).toList();
        if (skillNames.isEmpty()) {
            return ResponseEntity.ok("Start studying some topics first to get personalized recommendations!");
        }
        return ResponseEntity.ok(geminiService.getRecommendations(skillNames));
    }
}

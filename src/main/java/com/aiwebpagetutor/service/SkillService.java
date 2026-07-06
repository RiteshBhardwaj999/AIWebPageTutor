package com.aiwebpagetutor.service;

import com.aiwebpagetutor.model.Skill;
import com.aiwebpagetutor.model.User;
import com.aiwebpagetutor.repository.SkillRepository;
import com.aiwebpagetutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public Skill trackSkill(String email, String skillName, String category) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return skillRepository.findByUserIdAndNameIgnoreCase(user.getId(), skillName)
                .map(existing -> {
                    existing.setInteractionCount(existing.getInteractionCount() + 1);
                    existing.setLastStudied(LocalDateTime.now());
                    return skillRepository.save(existing);
                })
                .orElseGet(() -> skillRepository.save(
                        Skill.builder()
                                .user(user)
                                .name(skillName)
                                .category(category)
                                .interactionCount(1)
                                .build()
                ));
    }

    public List<Skill> getSkills(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return skillRepository.findByUserIdOrderByLastStudiedDesc(user.getId());
    }
}

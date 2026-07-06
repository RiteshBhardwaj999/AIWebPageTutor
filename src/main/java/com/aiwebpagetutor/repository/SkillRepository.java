package com.aiwebpagetutor.repository;

import com.aiwebpagetutor.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUserIdOrderByLastStudiedDesc(Long userId);
    Optional<Skill> findByUserIdAndNameIgnoreCase(Long userId, String name);
}

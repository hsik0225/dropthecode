package com.wootech.dropthecode.service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.wootech.dropthecode.domain.Skill;
import com.wootech.dropthecode.repository.SkillRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Skill> findAllToMap() {
        return skillRepository.findAll()
                              .stream()
                              .collect(Collectors.toMap(Skill::getName, Function.identity()));
    }
}

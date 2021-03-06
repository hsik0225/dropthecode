package com.wootech.dropthecode.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;

import com.wootech.dropthecode.domain.Language;
import com.wootech.dropthecode.domain.Skill;
import com.wootech.dropthecode.domain.bridge.LanguageSkill;
import com.wootech.dropthecode.exception.TeacherException;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TechSpec {

    /**
     * 선생님 프로그래밍 언어
     */
    @NotBlank
    private String language;

    /**
     * 선생님 기술 스택
     */
    private List<String> skills = new ArrayList<>();

    @Builder
    public TechSpec(String language, List<String> skills) {
        this.language = language;
        this.skills = skills;
    }

    public void validateSkillsInLanguage(Language language) {
        List<String> collect = language.getSkills()
                                       .stream()
                                       .map(LanguageSkill::getSkill)
                                       .map(Skill::getName)
                                       .collect(Collectors.toList());

        if (!collect.containsAll(skills)) {
            throw new TeacherException("언어에 포함되지 않는 기술이 있습니다.");
        }
    }
}

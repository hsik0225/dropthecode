package com.wootech.dropthecode.service;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;

import com.wootech.dropthecode.domain.*;
import com.wootech.dropthecode.dto.TechSpec;
import com.wootech.dropthecode.dto.request.TeacherFilterRequest;
import com.wootech.dropthecode.dto.request.TeacherRegistrationRequest;
import com.wootech.dropthecode.dto.response.TeacherPaginationResponse;
import com.wootech.dropthecode.dto.response.TeacherProfileResponse;
import com.wootech.dropthecode.exception.TeacherException;
import com.wootech.dropthecode.repository.TeacherProfileRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private final MemberService memberService;
    private final LanguageService languageService;
    private final SkillService skillService;
    private final TeacherLanguageService teacherLanguageService;
    private final TeacherSkillService teacherSkillService;
    private final TeacherProfileRepository teacherProfileRepository;

    public TeacherService(MemberService memberService, LanguageService languageService,
                          SkillService skillService,
                          TeacherLanguageService teacherLanguageService,
                          TeacherSkillService teacherSkillService,
                          TeacherProfileRepository teacherProfileRepository) {
        this.memberService = memberService;
        this.languageService = languageService;
        this.skillService = skillService;
        this.teacherLanguageService = teacherLanguageService;
        this.teacherSkillService = teacherSkillService;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    @Transactional(readOnly = true)
    public TeacherProfile findById(Long id) {
        return teacherProfileRepository.findById(id)
                                       .orElseThrow(() -> new EntityNotFoundException("???????????? ?????? ??????????????????."));
    }

    @Transactional
    public TeacherProfile save(TeacherProfile teacherProfile) {
        return teacherProfileRepository.save(teacherProfile);
    }

    @Transactional
    public void delete(TeacherProfile teacherProfile) {
        teacherProfileRepository.delete(teacherProfile);
    }

    @Transactional
    public void registerTeacher(LoginMember loginMember, TeacherRegistrationRequest teacherRegistrationRequest) {
        Member member = memberService.findById(loginMember.getId());

        if (member.hasRole(Role.TEACHER)) {
            throw new TeacherException("?????? ???????????? ????????? ??????????????????.");
        }

        if (member.hasRole(Role.DELETED)) {
            throw new TeacherException("?????? ????????? ??????????????????.");
        }

        Map<String, Language> languageMap = languageService.findAllToMap();
        List<Language> languages = findLanguageByNames(teacherRegistrationRequest.getTechSpecs(), languageMap);
        List<Skill> skills = findSkillsByNames(teacherRegistrationRequest.getTechSpecs(), skillService.findAllToMap());
        teacherRegistrationRequest.validateSkillsInLanguage(languageMap);

        final TeacherProfile teacher = teacherProfileRepository.findById(loginMember.getId())
                                                               .map(teacherProfile -> teacherProfile.update(teacherRegistrationRequest))
                                                               .orElseGet(() -> save(teacherRegistrationRequest.toTeacherProfileWithMember(member)));

        teacherLanguageService.saveAllWithTeacher(languages, teacher);
        teacherSkillService.saveAllWithTeacher(skills, teacher);
        member.setRole(Role.TEACHER);
        memberService.save(member);
    }

    private List<Language> findLanguageByNames(List<TechSpec> techSpecs, Map<String, Language> languageMap) {
        return techSpecs.stream()
                        .map(TechSpec::getLanguage)
                        .map(languageName -> Optional.ofNullable(languageMap.get(languageName)))
                        .map(languageOptional -> languageOptional.orElseThrow(() -> new TeacherException("???????????? ?????? ???????????????.")))
                        .collect(Collectors.toList());
    }

    private List<Skill> findSkillsByNames(List<TechSpec> techSpecs, Map<String, Skill> skillMap) {
        return techSpecs.stream()
                        .map(TechSpec::getSkills)
                        .flatMap(Collection::stream)
                        .map(skillName -> Optional.ofNullable(skillMap.get(skillName)))
                        .map(skill -> skill.orElseThrow(() -> new TeacherException("???????????? ?????? ???????????????.")))
                        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherPaginationResponse findAll(TeacherFilterRequest teacherFilterRequest, Pageable pageable) {
        Language language = findLanguageByNames(Collections.singletonList(teacherFilterRequest.getTechSpec()), languageService
                .findAllToMap()).get(0);
        List<Skill> skills = findSkillsByNames(Collections.singletonList(teacherFilterRequest.getTechSpec()), skillService
                .findAllToMap());

        Page<TeacherProfile> teacherProfilePage = teacherProfileRepository.findAll(
                language,
                skills,
                teacherFilterRequest.getCareer(),
                pageable
        );

        final List<TeacherProfileResponse> teacherProfileResponses = teacherProfilePage.stream()
                                                                                       .map(TeacherProfileResponse::from)
                                                                                       .collect(Collectors.toList());

        return new TeacherPaginationResponse(teacherProfileResponses, teacherProfilePage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public TeacherProfileResponse findTeacherResponseById(Long id) {
        return TeacherProfileResponse.from(findById(id));
    }

    @Transactional
    public void updateAverageReviewTime(Long id, Long reviewTime) {
        TeacherProfile teacher = findById(id);
        teacher.updateReviewCountAndTime(reviewTime);
    }

    @Transactional
    public void updateTeacher(LoginMember loginMember, TeacherRegistrationRequest teacherRegistrationRequest) {
        Map<String, Language> languageMap = languageService.findAllToMap();
        List<Language> languages = findLanguageByNames(teacherRegistrationRequest.getTechSpecs(), languageMap);
        List<Skill> skills = findSkillsByNames(teacherRegistrationRequest.getTechSpecs(), skillService.findAllToMap());
        teacherRegistrationRequest.validateSkillsInLanguage(languageMap);

        TeacherProfile teacher = findById(loginMember.getId());

        teacherLanguageService.deleteAllWithTeacher(teacher);
        teacherLanguageService.saveAllWithTeacher(languages, teacher);

        teacherSkillService.deleteAllWithTeacher(teacher);
        teacherSkillService.saveAllWithTeacher(skills, teacher);

        teacher.update(teacherRegistrationRequest.getTitle(), teacherRegistrationRequest.getContent(), teacherRegistrationRequest
                .getCareer());
    }

    @Transactional
    public void deleteTeacher(LoginMember loginMember) {
        Member member = memberService.findById(loginMember.getId());
        member.setRole(Role.STUDENT);
        memberService.save(member);

        TeacherProfile teacher = member.getTeacherProfile();
        delete(teacher);
    }
}


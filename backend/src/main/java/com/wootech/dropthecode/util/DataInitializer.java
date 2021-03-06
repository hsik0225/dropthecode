package com.wootech.dropthecode.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.wootech.dropthecode.domain.*;
import com.wootech.dropthecode.domain.bridge.LanguageSkill;
import com.wootech.dropthecode.domain.bridge.TeacherLanguage;
import com.wootech.dropthecode.domain.bridge.TeacherSkill;
import com.wootech.dropthecode.domain.review.Review;
import com.wootech.dropthecode.repository.*;
import com.wootech.dropthecode.repository.bridge.LanguageSkillRepository;
import com.wootech.dropthecode.repository.bridge.TeacherLanguageRepository;
import com.wootech.dropthecode.repository.bridge.TeacherSkillRepository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({"local-init", "prod-init"})
@Component
public class DataInitializer implements ApplicationRunner {
    private static final Random random = new Random();

    private final MemberRepository memberRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final LanguageRepository languageRepository;
    private final SkillRepository skillRepository;
    private final ReviewRepository reviewRepository;

    private final TeacherLanguageRepository teacherLanguageRepository;
    private final TeacherSkillRepository teacherSkillRepository;
    private final LanguageSkillRepository languageSkillRepository;

    private final Environment environment;

    public DataInitializer(MemberRepository memberRepository, TeacherProfileRepository teacherProfileRepository, LanguageRepository languageRepository, SkillRepository skillRepository, ReviewRepository reviewRepository, TeacherLanguageRepository teacherLanguageRepository, TeacherSkillRepository teacherSkillRepository, LanguageSkillRepository languageSkillRepository, Environment environment) {
        this.memberRepository = memberRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.languageRepository = languageRepository;
        this.skillRepository = skillRepository;
        this.reviewRepository = reviewRepository;
        this.teacherLanguageRepository = teacherLanguageRepository;
        this.teacherSkillRepository = teacherSkillRepository;
        this.languageSkillRepository = languageSkillRepository;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<Long, Language> languageMap = insertLanguage()
                .stream()
                .collect(Collectors.toMap(Language::getId, Function.identity()));

        Map<Long, Skill> skillMap = insertSkill()
                .stream()
                .collect(Collectors.toMap(Skill::getId, Function.identity()));

        insertLanguageSkill(languageMap, skillMap);

        if (!environment.acceptsProfiles(Profiles.of("local-init"))) {
            return;
        }

        Map<Long, Member> memberMap = insertMember()
                .stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        Map<Long, TeacherProfile> teacherMap = insertTeacherProfile(memberMap)
                .stream()
                .collect(Collectors.toMap(TeacherProfile::getId, Function.identity()));

        insertTeacherLanguage(languageMap, teacherMap);
        insertTeacherSkill(skillMap, teacherMap);

        insertReview(memberMap);
    }

    private List<Language> insertLanguage() {
        List<Language> languages = Arrays.asList(
                Language.builder().name("java").build(),
                Language.builder().name("javascript").build(),
                Language.builder().name("python").build(),
                Language.builder().name("kotlin").build(),
                Language.builder().name("c").build()
        );
        return languageRepository.saveAll(languages);
    }

    private List<Skill> insertSkill() {
        List<Skill> skills = Arrays.asList(
                Skill.builder().name("spring").build(),
                Skill.builder().name("vue").build(),
                Skill.builder().name("react").build(),
                Skill.builder().name("angular").build(),
                Skill.builder().name("django").build()
        );
        return skillRepository.saveAll(skills);
    }

    private void insertLanguageSkill(Map<Long, Language> languageMap, Map<Long, Skill> skillMap) {
        List<LanguageSkill> languageSkills = Arrays.asList(
                new LanguageSkill(languageMap.get(1L), skillMap.get(1L)),
                new LanguageSkill(languageMap.get(2L), skillMap.get(2L)),
                new LanguageSkill(languageMap.get(2L), skillMap.get(3L)),
                new LanguageSkill(languageMap.get(2L), skillMap.get(4L)),
                new LanguageSkill(languageMap.get(3L), skillMap.get(5L)),
                new LanguageSkill(languageMap.get(4L), skillMap.get(1L))
        );
        languageSkillRepository.saveAll(languageSkills);
    }

    private List<Member> insertMember() {
        List<Member> members = Arrays.asList(
                realMember("67591151", "shinse@gmail.com", "Shinsehantan", "https://avatars.githubusercontent.com/u/50273712?v=44", "https://github.com/shinsehantan", Role.STUDENT),

                realMember("45876793", "air@gmail.com", "Air", "https://avatars.githubusercontent.com/u/45876793?v=4", "https://github.com/KJunseo", Role.TEACHER),
                realMember("56301069", "seed@gmail.com", "Seed", "https://avatars.githubusercontent.com/u/56301069?v=4", "https://github.com/hsik0225", Role.TEACHER),
                realMember("32974201", "allie@gmail.com", "Allie", "https://avatars.githubusercontent.com/u/32974201?v=4", "https://github.com/jh8579", Role.TEACHER),
                realMember("52202474", "bran@gmail.com", "Bran", "https://avatars.githubusercontent.com/u/52202474?v=4", "https://github.com/seojihwan", Role.TEACHER),
                realMember("50273712", "fafi@gmail.com", "Fafi", "https://avatars.githubusercontent.com/u/50273712?v=44", "https://github.com/TaewanKimmmm", Role.TEACHER),
                dummyMember("calvin0627@naver.com", "Calvin", "https://avatars.githubusercontent.com/u/29232608?v=4", "https://github.com/calvin0627", Role.TEACHER),

                dummyMember("syndra9106@naver.com", "Syndra", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Syndra_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("anivia0627@naver.com", "Anivia", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Anivia_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("faker96@gmail.com", "Faker", "https://file.newswire.co.kr/data/datafile2/thumb_640/2021/06/1981906469_20210610110159_5423614022.jpg", "https://github.com", Role.TEACHER),
                dummyMember("hotcurry@gmail.com", "Curry", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxO9huSTvjzYvTKMdAhbm53dnrlNyLFzBnyw&usqp=CAU", "https://github.com", Role.TEACHER),
                dummyMember("fiora119@gmail.com", "Fiora", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Fiora_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("mushroom@gmail.com", "Teemo", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Teemo_0.jpg", "https://github.com", Role.TEACHER),

                dummyMember("talonkillyou@naver.com", "Talon", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Talon_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("lasthit@naver.com", "Karthus", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Karthus_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("airplane33@gmail.com", "Corki", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Corki_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("icearrow@gmail.com", "Ashe", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ashe_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("gonggipang@gmail.com", "Orianna", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Orianna_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("prison@gmail.com", "Thresh", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Thresh_0.jpg", "https://github.com", Role.TEACHER),

                dummyMember("harp33@naver.com", "Sona", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Sona_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("mosquito@naver.com", "Vladimir", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Vladimir_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("laser@gmail.com", "Viktor", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Viktor_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("beinchung@gmail.com", "Vayne", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Vayne_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("madlife@gmail.com", "Blitzcrank", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Blitzcrank_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("manhgomanheun@gmail.com", "Ezreal", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ezreal_0.jpg", "https://github.com", Role.TEACHER),

                dummyMember("gumiho99@naver.com", "Ahri", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Ahri_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("ninja1@naver.com", "Akali", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Akali_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("turnofflight@gmail.com", "Nocturne", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Nocturne_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("harrypotter1111@gmail.com", "LeBlanc", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Leblanc_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("moon33@gmail.com", "Diana", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Diana_0.jpg", "https://github.com", Role.TEACHER),
                dummyMember("drmundo@gmail.com", "Mundo", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/DrMundo_0.jpg", "https://github.com", Role.TEACHER)
        );

        return memberRepository.saveAll(members);
    }

    private Member realMember(String oauthId, String email, String name, String imageUrl, String githubUrl, Role role) {
        return Member.builder()
                     .oauthId(oauthId)
                     .email(email)
                     .name(name)
                     .imageUrl(imageUrl)
                     .githubUrl(githubUrl)
                     .role(role)
                     .build();
    }

    private Member dummyMember(String email, String name, String imageUrl, String githubUrl, Role role) {
        return Member.builder()
                     .email(email)
                     .name(name)
                     .imageUrl(imageUrl)
                     .githubUrl(githubUrl)
                     .role(role)
                     .build();
    }

    private List<TeacherProfile> insertTeacherProfile(Map<Long, Member> memberMap) {
        List<TeacherProfile> teacherProfiles = Arrays.asList(
                dummyTeacherProfile("?????????????????? ????????? ?????????", "?????? ??????, ?????? ????????? ???????????????.\n" + "???????????? ?????? ????????? ??? ?????? ??????????????? ?????????????????????.", random
                        .nextInt(20) + 1, random.nextInt(100) + 1, Math.round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap
                        .get(2L)),
                dummyTeacherProfile("?????????????????? ????????? ?????????", "?????? ??? ?????? ????????? ?????? ???????????? ???????????????.\n" + "????????? ?????? ???????????????.", random.nextInt(20) + 1, random
                        .nextInt(100) + 1, Math.round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(3L)),
                dummyTeacherProfile("?????????????????? ????????? ?????????", "????????? ????????? ?????? ???????????????.\n" + "?????? ????????? ???????????? ???????????? ????????????.", random.nextInt(20) + 1, random
                        .nextInt(100) + 1, Math.round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(4L)),
                dummyTeacherProfile("?????????????????? ??????????????? ?????????", "???????????? ?????????????????????.\n" + "?????? ????????? ???????????????.\n" + "???????????? ????????? ?????????.", random.nextInt(20) + 1, random
                        .nextInt(100) + 1, Math.round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(5L)),
                dummyTeacherProfile("?????????????????? ????????? ?????????", "????????? ????????? ????????? ??????????????????!\n" + "?????? ????????? ?????? ????????? ????????????.", random.nextInt(20) + 1, random
                        .nextInt(100) + 1, Math.round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(6L)),
                dummyTeacherProfile("????????? ?????? ????????? ?????????", "???????????? ??????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(7L)),

                dummyTeacherProfile("????????? ?????????????????? ?????? ??????", "????????? 1??? ??????. ????????? ???????????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(8L)),
                dummyTeacherProfile("?????? ?????? ??? ?????????", "????????? ?????? ????????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(9L)),
                dummyTeacherProfile("ios ?????????", "ios??? ?????? ?????? ?????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(10L)),
                dummyTeacherProfile("????????? ?????? ?????? ??????", "??? ?????? ????????? ?????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(11L)),
                dummyTeacherProfile("Amazon ?????? ?????????", "???????????? ?????? ??????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(12L)),
                dummyTeacherProfile("????????? ????????? ?????????", "????????? ????????? ????????? ???????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(13L)),

                dummyTeacherProfile("???????????? ????????? ?????????", "???????????? 1??? ??????. ????????? ???????????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(14L)),
                dummyTeacherProfile("?????? ????????? ?????????", "???????????? ????????? ???????????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(15L)),
                dummyTeacherProfile("?????? ?????? ????????? ?????????", "????????? ???????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math.round((random
                        .nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(16L)),
                dummyTeacherProfile("????????? ???????????? ?????????", "??????????????? ?????? ????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(17L)),
                dummyTeacherProfile("?????? ????????? ?????????", "????????? ?????????????????????", random.nextInt(20) + 1, random.nextInt(100) + 1, Math.round((random
                        .nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(18L)),
                dummyTeacherProfile("???????????? ??????????????? ?????????", "?????? ????????? ????????? ?????????", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(19L)),

                dummyTeacherProfile("???????????? ?????????", "???????????? ??????????????????. ????????? ???????????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(20L)),
                dummyTeacherProfile("????????? ??????????????? ?????????", "??????????????? ???????????? ??????????????????? ?????? ???????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(21L)),
                dummyTeacherProfile("?????? ??? ????????? ?????????", "?????? ??????????????? ????????????", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(22L)),
                dummyTeacherProfile("????????? ???????????? ?????????", "???????????? ???????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math.round((random
                        .nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(23L)),
                dummyTeacherProfile("????????? ?????? ?????? ????????? ?????????", "??????????????? ?????? ????????? ?????????. ????????? ???????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(24L)),
                dummyTeacherProfile("opgg ?????? ?????????", "?????????????????? ?????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(25L)),

                dummyTeacherProfile("????????? ?????? ?????????", "????????? ??????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math.round((random
                        .nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(26L)),
                dummyTeacherProfile("????????? ?????? ios ?????????", "????????? ?????? ??? ????????? ????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(27L)),
                dummyTeacherProfile("??????????????? ?????????", "?????????????????? ?????? ??? ?????? ?????????????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(28L)),
                dummyTeacherProfile("????????????????????? ?????????", "?????????????????? ????????? ?????? ??? ?????????", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(29L)),
                dummyTeacherProfile("????????? ?????? ?????????", "???????????? ????????? ??? ??? ?????? ???????????????.", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(30L)),
                dummyTeacherProfile("??????????????? ??????????????? ?????????", "????????? ????????? ????????????!", random.nextInt(20) + 1, random.nextInt(100) + 1, Math
                        .round((random.nextDouble() * 7 + 1) * 10) / 10.0, memberMap.get(31L))
        );
        return teacherProfileRepository.saveAll(teacherProfiles);
    }

    private TeacherProfile dummyTeacherProfile(String title, String content, Integer career, Integer sumReviewCount, Double averageReviewTime, Member member) {
        return TeacherProfile.builder()
                             .title(title)
                             .content(content)
                             .career(career)
                             .sumReviewCount(sumReviewCount)
                             .averageReviewTime(averageReviewTime)
                             .member(member)
                             .build();
    }

    private void insertTeacherLanguage(Map<Long, Language> languageMap, Map<Long, TeacherProfile> teacherMap) {
        List<TeacherLanguage> teacherLanguages = Arrays.asList(
                new TeacherLanguage(teacherMap.get(2L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(2L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(2L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(3L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(3L), languageMap.get(4L)),
                new TeacherLanguage(teacherMap.get(3L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(4L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(4L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(4L), languageMap.get(4L)),

                new TeacherLanguage(teacherMap.get(5L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(5L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(5L), languageMap.get(4L)),

                new TeacherLanguage(teacherMap.get(6L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(6L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(6L), languageMap.get(3L)),

                new TeacherLanguage(teacherMap.get(7L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(7L), languageMap.get(2L)),

                new TeacherLanguage(teacherMap.get(8L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(8L), languageMap.get(4L)),

                new TeacherLanguage(teacherMap.get(9L), languageMap.get(4L)),
                new TeacherLanguage(teacherMap.get(9L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(10L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(10L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(11L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(11L), languageMap.get(4L)),

                new TeacherLanguage(teacherMap.get(12L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(12L), languageMap.get(4L)),

                new TeacherLanguage(teacherMap.get(13L), languageMap.get(4L)),
                new TeacherLanguage(teacherMap.get(13L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(14L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(14L), languageMap.get(3L)),

                new TeacherLanguage(teacherMap.get(15L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(15L), languageMap.get(4L)),

                new TeacherLanguage(teacherMap.get(16L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(17L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(18L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(19L), languageMap.get(4L)),
                new TeacherLanguage(teacherMap.get(20L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(21L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(22L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(23L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(24L), languageMap.get(4L)),
                new TeacherLanguage(teacherMap.get(25L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(26L), languageMap.get(1L)),
                new TeacherLanguage(teacherMap.get(27L), languageMap.get(2L)),
                new TeacherLanguage(teacherMap.get(28L), languageMap.get(3L)),
                new TeacherLanguage(teacherMap.get(29L), languageMap.get(4L)),
                new TeacherLanguage(teacherMap.get(30L), languageMap.get(5L)),

                new TeacherLanguage(teacherMap.get(31L), languageMap.get(1L))
        );
        teacherLanguageRepository.saveAll(teacherLanguages);
    }

    private void insertTeacherSkill(Map<Long, Skill> skillMap, Map<Long, TeacherProfile> teacherMap) {
        List<TeacherSkill> teacherSkills = Arrays.asList(
                new TeacherSkill(teacherMap.get(2L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(2L), skillMap.get(2L)),
                new TeacherSkill(teacherMap.get(2L), skillMap.get(3L)),

                new TeacherSkill(teacherMap.get(3L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(4L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(4L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(5L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(5L), skillMap.get(3L)),
                new TeacherSkill(teacherMap.get(5L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(6L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(6L), skillMap.get(2L)),
                new TeacherSkill(teacherMap.get(6L), skillMap.get(3L)),
                new TeacherSkill(teacherMap.get(6L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(7L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(7L), skillMap.get(2L)),

                new TeacherSkill(teacherMap.get(8L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(8L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(9L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(10L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(11L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(11L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(12L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(13L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(14L), skillMap.get(2L)),
                new TeacherSkill(teacherMap.get(14L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(15L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(15L), skillMap.get(5L)),

                new TeacherSkill(teacherMap.get(16L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(17L), skillMap.get(2L)),
                new TeacherSkill(teacherMap.get(18L), skillMap.get(5L)),
                new TeacherSkill(teacherMap.get(19L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(21L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(22L), skillMap.get(2L)),
                new TeacherSkill(teacherMap.get(23L), skillMap.get(5L)),
                new TeacherSkill(teacherMap.get(24L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(26L), skillMap.get(1L)),
                new TeacherSkill(teacherMap.get(27L), skillMap.get(2L)),
                new TeacherSkill(teacherMap.get(28L), skillMap.get(5L)),
                new TeacherSkill(teacherMap.get(29L), skillMap.get(1L)),

                new TeacherSkill(teacherMap.get(31L), skillMap.get(1L))
        );
        teacherSkillRepository.saveAll(teacherSkills);
    }

    private void insertReview(Map<Long, Member> memberMap) {
        List<Review> reviews = Arrays.asList(
                dummyReview(memberMap.get(3L), memberMap.get(2L), "[1?????? - ????????? ?????? ??????] ??????(?????????) ?????? ???????????????.", "???????????????! ???????????? ??????! ?????? ?????????????????? ??? ???????????????!!\n" +
                        "?????? ????????? ??? ?????? ????????? ??? ????????? ?????? ???????????? ????????? ?????? ????????? ??????", "https://github.com/woowacourse/java-racingcar/pull/159", 3L, Progress.ON_GOING),

                dummyReview(memberMap.get(4L), memberMap.get(2L), "[1?????? - ?????? ??????] ??????(?????????) ?????? ???????????????.", "???????????????! ??????????????????! ???????????????\n" +
                        "?????? ?????? ??? ?????????????????? ??????\n" +
                        "\n" +
                        "????????? ?????? ???????????? ????????????????????? ??????????????????. ???????????? ?????? ????????? ????????? ?????? ?????? ????????? ??????????????? ????????? ???????????????!\n" +
                        "?????? ?????? ????????? ???????????? ?????? ????????? ??? ?????? ??? ????????? ??????????????? ??????\n" +
                        "?????? ????????? ???????????????", "https://github.com/woowacourse/java-lotto/pull/268", 2L, Progress.ON_GOING),

                dummyReview(memberMap.get(5L), memberMap.get(2L), "[2?????? - todo list] ??????(?????????) ?????? ???????????????.", "???????????????! ???????????????!\n" +
                        "?????????????????? ?????? ???????????? ?????????.", "https://github.com/woowacourse/js-todo-list-step2/pull/6", 1L, Progress.ON_GOING),

                dummyReview(memberMap.get(6L), memberMap.get(2L), "[1?????? - ????????? ??????] ??????(?????????) ?????? ???????????????. ", "???????????????!!  ???????????? ??????! ??? ??????????????????\n????????? ?????? ?????? ?????? ?????? ????????? ??? ???????????????\n" +
                        "?????? ?????? ????????? ???????????? ?????? ?????? ?????? ???????????? ????????????! ?????? ?????? ????????? ???????????????~~\n" +
                        "????????? ?????? ?????? ????????? ?????????????????? ????????????. ?????? ????????? ?????? ??????????????????!!\n", "https://github.com/woowacourse/java-blackjack/pull/141", 3L, Progress.TEACHER_COMPLETED),

                dummyReview(memberMap.get(7L), memberMap.get(2L), "[1, 2, 3?????? - ??????] ??????(?????????) ?????? ???????????????. ", "???????????????! ???????????????! ??? ???????????????\n" +
                        "?????? ????????? ?????????????????????\n" +
                        "????????? ???????????? ?????????????????? ??????!! \n" +
                        "?????? ????????? ????????????!", "https://github.com/woowacourse/java-chess/pull/190", 2L, Progress.TEACHER_COMPLETED),

                dummyReview(memberMap.get(8L), memberMap.get(2L), "[1, 2?????? - Spring ????????????] ??????(?????????) ?????? ???????????????.", "???????????????! ?????? ????????? ???????????? ??? ??????????????????!!\n??? ??????????????????!\n" +
                        "????????? ???????????? ??????????????? ????????? ????????? ??????????????? ????????????????????? ?????? ?????????????????? ??????????????? ?????? ??? ?????????.\n?????? ????????? ????????? ?????? ???????????? ???????????? ?????????!! ?????? ????????? ???????????????!!", "https://github.com/woowacourse/jwp-chess/pull/233", 2L, Progress.FINISHED),

                dummyReview(memberMap.get(9L), memberMap.get(2L), "[Spring ????????? ????????? ?????? - 1, 2??????] ??????(?????????) ?????? ???????????????. ", "???????????????! ???????????? ??????! ????????? ??? ??????????????????\n" +
                        "????????? ????????? ??????????????????, ????????? ????????? ?????? ?????? ??? ????????????.. ??????????????? ?????? ???????????? ????????? ?????? ????????? ?????? ??? ???????????????. ????????? ????????? ??? ???????????? ?????????????????????!!", "https://github.com/woowacourse/atdd-subway-map/pull/71", 3L, Progress.FINISHED),

                dummyReview(memberMap.get(10L), memberMap.get(2L), "[Spring ????????? ?????? ?????? - 1,2??????] ??????(?????????) ?????? ???????????????. ", "???????????????! ???????????????!\n" +
                        "JWT??? ?????? ??????????????? ????????? ?????? ??? ?????????????????????\n" +
                        "????????? ??? ???????????????!!", "https://github.com/woowacourse/atdd-subway-path/pull/71", 3L, Progress.FINISHED),

                dummyReview(memberMap.get(11L), memberMap.get(2L), "[????????? - ?????? ??????] ??????(?????????) ?????? ???????????????.", "???????????????!! ???????????? ?????????! ?????? ?????????!\n" +
                        "????????? ???????????????! ?????? ??? ???????????????!!\n", "https://github.com/woowacourse/atdd-subway-fare/pull/29", 2L, Progress.FINISHED),

                dummyReview(memberMap.get(12L), memberMap.get(2L), "?????? ?????? ?????? ?????? ?????? ?????? ??????", "?????? ?????? ?????? ?????? ?????? ?????? ??????\n" +
                        "\n" +
                        "query dsl??? ???????????? ?????? ?????? ??????\n" +
                        "\n" +
                        "??????????????????, sort??? ?????? ???????????? ?????? QueryDsl ?????? ????????? ????????? ??????\n" +
                        "where??? ??????????????? ?????? ?????? ??????\n" +
                        "?????? ????????? ?????????, ????????? ???????????? ?????? ?????????, ?????? ????????? ?????? ?????????(????????? ?????? ?????? ??????)", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/153", 1L, Progress.FINISHED),

                dummyReview(memberMap.get(2L), memberMap.get(4L), "?????? ?????? ???????????? ?????? ??????", "?????? ??????\n" +
                        "?????? ?????? ????????????\n" +
                        "?????? ??????\n" +
                        "??????????????? ?????? ??????????????? ???????????? final??? ????????? ?????? ?????? ??????\n" +
                        "?????? ??????\n" +
                        "TeacherProfile ID??? Member ID??? ????????? ?????? ?????? ??? ?????? ????????? ?????? ??? ???????????????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/157", 2L, Progress.ON_GOING),

                dummyReview(memberMap.get(2L), memberMap.get(6L), "?????? ?????? ?????? ??????", "?????? ?????? ????????? ???????????? ??????????????????!!!!!!!\n" +
                        "\n" +
                        "???????????? ??? ??????\n" +
                        "?????? ?????? ??? ???????????? ?????? ????????? ????????? ????????? ????????? ???????????? ??? ??? ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/163", 1L, Progress.ON_GOING),

                dummyReview(memberMap.get(2L), memberMap.get(4L), "TeacherProfilie DB??? Member ID ???????????? ?????? ?????? ??????", "????????????\n" +
                        "??????????????? Member Id ??????????????? ?????? ??????\n" +
                        "@TaewanKimmmm ?????? ????????? ??????\n" +
                        "????????? ????????? ??????\n" +
                        "???????????? DB ????????? ??????\n" +
                        "local properties?????? ddl-auto create?????? ??????\n" +
                        "DB DataInitializer ActiveProfiles??? \"local\" ??????\n" +
                        "???????????? DB ????????? ???\n" +
                        "?????? ?????? ??????\n" +
                        "?????? ???????????? DB ????????? ??????\n" +
                        "????????? ?????? Jenkins-init-deploy.sh ?????? -> ActiveProfiles??? \"prod-init\"?????? ???????????? ??????\n" +
                        "???????????? DB ????????? ???\n" +
                        "?????? Jenkins-deploy.sh ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/158", 2L, Progress.ON_GOING),

                dummyReview(memberMap.get(2L), memberMap.get(4L), "review ????????? ????????? ?????? ??????", "?????? ??????\n" +
                        "Review ?????? ????????? ??????????????? ?????? ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/150", 3L, Progress.ON_GOING),

                dummyReview(memberMap.get(2L), memberMap.get(6L), "ReviewController ?????? ????????? URI ??????", "ReviewController ?????? ????????? URI ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/171", 3L, Progress.TEACHER_COMPLETED),

                dummyReview(memberMap.get(2L), memberMap.get(6L), "members/me, ????????? ?????? ??????, ????????? ?????? ?????? ??? ????????? github url ?????? ??????", "??????????????????~", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/198", 1L, Progress.TEACHER_COMPLETED),

                dummyReview(memberMap.get(2L), memberMap.get(3L), "API ?????????", "rest docs ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/37", 2L, Progress.FINISHED),

                dummyReview(memberMap.get(2L), memberMap.get(3L), "????????? ?????? ??????", "????????? ?????? ?????? ?????? ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/164", 2L, Progress.FINISHED),

                dummyReview(memberMap.get(2L), memberMap.get(3L), "????????? ?????? ?????? ?????? ??? ????????? ?????? ??????", "?????? ?????? ??? ?????? ????????? ?????? ??????\n" +
                        "?????? ?????????(ex. langauge)??? ?????? ??????\n" +
                        "DB??? ?????? ?????? ?????? ????????? ????????? ??????\n" +
                        "???????????? ?????? ?????? ??????, ??? TeacherProfile??? ????????? ?????? ?????? ?????? ????????? ????????? ????????? ??? ??????\n" +
                        "@MapsId??? ???????????? TeacherID??? MemberID??? ????????? ?????? ??????", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/156", 3L, Progress.FINISHED),

                dummyReview(memberMap.get(2L), memberMap.get(3L), "Logback?????? Error ??????, Info ?????? ?????????", "?????? ??????!", "https://github.com/woowacourse-teams/2021-drop-the-code/pull/182", 3L, Progress.FINISHED)
        );
        reviewRepository.saveAll(reviews);
    }

    private Review dummyReview(Member teacher, Member student, String title, String content, String prUrl, Long elapsedTime, Progress progress) {
        return Review.builder()
                     .teacher(teacher)
                     .student(student)
                     .title(title)
                     .content(content)
                     .prUrl(prUrl)
                     .elapsedTime(elapsedTime)
                     .progress(progress)
                     .build();
    }
}

package com.wootech.dropthecode.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;

import com.wootech.dropthecode.domain.bridge.TeacherLanguage;
import com.wootech.dropthecode.domain.bridge.TeacherSkill;
import com.wootech.dropthecode.dto.request.TeacherRegistrationRequest;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class TeacherProfile {
    @Id
    private Long id;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer career;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", unique = true, foreignKey = @ForeignKey(name = "fk_teacherProfile_to_member"))
    private Member member;

    @OneToMany(mappedBy = "teacherProfile", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("language.id")
    private Set<TeacherLanguage> languages = new HashSet<>();

    @OneToMany(mappedBy = "teacherProfile", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("skill.id")
    private Set<TeacherSkill> skills = new HashSet<>();

    @Column(columnDefinition = "integer default 0")
    private Integer sumReviewCount = 0;

    private Double averageReviewTime = (double) 0;

    @Builder
    public TeacherProfile(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String title, String content, Integer career, Member member, Set<TeacherLanguage> languages, Set<TeacherSkill> skills, Integer sumReviewCount, Double averageReviewTime) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
        this.content = content;
        this.career = career;
        this.member = member;
        this.languages = languages;
        this.skills = skills;
        this.sumReviewCount = sumReviewCount;
        this.averageReviewTime = averageReviewTime;
    }

    public void update(String title, String content, int career) {
        this.title = title;
        this.content = content;
        this.career = career;
    }

    public TeacherProfile update(TeacherRegistrationRequest teacherRegistrationRequest) {
        update(teacherRegistrationRequest.getTitle(), teacherRegistrationRequest.getContent(), teacherRegistrationRequest.getCareer());
        return this;
    }

    public void updateReviewCountAndTime(Long newReviewTime) {
        if (Objects.isNull(averageReviewTime)) {
            averageReviewTime = (double) 0;
        }

        double newAverageReviewTime = (newReviewTime + averageReviewTime * sumReviewCount * 24) / 24 / (sumReviewCount + 1);
        sumReviewCount++;
        averageReviewTime = Math.round(newAverageReviewTime * 10) / 10.0;
    }

    public void deleteWithMember() {
        this.title = "????????? ??????????????????.";
        this.content = "?????? ??????";
        this.career = 0;
    }
}

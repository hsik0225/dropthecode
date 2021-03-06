package com.wootech.dropthecode.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

import com.wootech.dropthecode.domain.chatting.Room;
import com.wootech.dropthecode.domain.review.Review;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {
    private static final String DELETED_USER_EMAIL = "unknown@dropthecode.co.kr";
    private static final String DELETED_USER_NAME = "탈퇴한 사용자";
    private static final String DELETED_USER_IMAGE_URL = "https://static.thenounproject.com/png/994628-200.png";

    private String oauthId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    private String githubUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private TeacherProfile teacherProfile;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Review> reviewsAsTeacher;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Review> reviewsAsStudent;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Room> roomAsTeacher;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Room> roomAsStudent;

    @Builder
    public Member(Long id, String oauthId, String email, String name, String imageUrl, String githubUrl, Role role, TeacherProfile teacherProfile, List<Review> reviewsAsTeacher, List<Review> reviewsAsStudent, LocalDateTime createdAt, List<Room> roomAsStudent, List<Room> roomAsTeacher) {
        super(id, createdAt);
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.githubUrl = githubUrl;
        this.role = role;
        this.teacherProfile = teacherProfile;
        this.reviewsAsTeacher = reviewsAsTeacher;
        this.reviewsAsStudent = reviewsAsStudent;
        this.roomAsStudent = roomAsStudent;
        this.roomAsTeacher = roomAsTeacher;
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public boolean hasSameId(Long id) {
        return this.id.equals(id);
    }

    public Member update(String email, String name, String imageUrl) {
        if (this.role == Role.DELETED) {
            this.role = Role.STUDENT;
        }
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        return this;
    }

    public void delete() {
        if (this.role == Role.TEACHER) {
            this.teacherProfile.deleteWithMember();
        }

        this.email = DELETED_USER_EMAIL;
        this.name = DELETED_USER_NAME;
        this.imageUrl = DELETED_USER_IMAGE_URL;
        this.role = Role.DELETED;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void addReviewAsTeacher(Review review) {
        reviewsAsTeacher.add(review);
    }
}

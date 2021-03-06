package com.wootech.dropthecode.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.wootech.dropthecode.config.JpaConfig;
import com.wootech.dropthecode.domain.Member;
import com.wootech.dropthecode.domain.Progress;
import com.wootech.dropthecode.domain.Role;
import com.wootech.dropthecode.domain.TeacherProfile;
import com.wootech.dropthecode.domain.review.Review;
import com.wootech.dropthecode.dto.ReviewSummary;
import com.wootech.dropthecode.dto.request.ReviewSearchCondition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.wootech.dropthecode.builder.MemberBuilder.dummyMember;
import static com.wootech.dropthecode.builder.ReviewBuilder.dummyReview;
import static com.wootech.dropthecode.builder.TeacherProfileBuilder.dummyTeacherProfile;
import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@DataJpaTest
class ReviewRepositoryCustomImplTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private AuditingEntityListener auditingEntityListener;

    private Member airTe;
    private Member allieTe;
    private Member seedStu;
    private Member fafiStu;

    private Review airSeed1;
    private Review allieSeed1;
    private Review airFafi1;
    private Review allieFafi1;
    private Review airSeed2;
    private Review airFafi2;
    private Review allieSeed2;
    private Review airFafi3;
    private Review airSeed3;
    private Review allieFafi2;
    private Review allieSeed3;

    @BeforeEach
    void setUp() {

        // given
        airTe = dummyMember("oauth1", "email1@gmail.com", "name1", "s3://imageUrl1",
                "github url1", Role.TEACHER, LocalDateTime.now());
        allieTe = dummyMember("oauth2", "email2@gmail.com", "name2", "s3://imageUrl2",
                "github url2", Role.TEACHER, LocalDateTime.now());

        TeacherProfile teacherProfile1 = dummyTeacherProfile("title1", "content1", 10,
                airTe, LocalDateTime.now());
        TeacherProfile teacherProfile2 = dummyTeacherProfile("title2", "content2", 20,
                allieTe, LocalDateTime.now());

        seedStu = dummyMember("oauth3", "email3@gmail.com", "name3", "s3://imageUrl3",
                "github url3", Role.STUDENT, LocalDateTime.now());
        fafiStu = dummyMember("oauth4", "email4@gmail.com", "name4", "s3://imageUrl4",
                "github url4", Role.STUDENT, LocalDateTime.now());

        em.persist(airTe);
        em.persist(allieTe);
        em.persist(seedStu);
        em.persist(fafiStu);
        em.persist(teacherProfile1);
        em.persist(teacherProfile2);

        airSeed1 = dummyReview(airTe, seedStu, "review title1", "review content1", "prUrl1",
                3L, Progress.ON_GOING, LocalDateTime.now());
        allieSeed1 = dummyReview(allieTe, seedStu, "review title2", "review content2", "prUrl2",
                4L, Progress.FINISHED, LocalDateTime.now().plusDays(1L));
        airFafi1 = dummyReview(airTe, fafiStu, "review title3", "review content3", "prUrl3",
                4L, Progress.ON_GOING, LocalDateTime.now().plusDays(2L));
        allieFafi1 = dummyReview(allieTe, fafiStu, "review title4", "review content4", "prUrl4",
                7L, Progress.ON_GOING, LocalDateTime.now().plusDays(3L));
        airSeed2 = dummyReview(airTe, seedStu, "review title5", "review content5", "prUrl5",
                8L, Progress.ON_GOING, LocalDateTime.now().plusDays(4L));
        airFafi2 = dummyReview(airTe, fafiStu, "review title6", "review content6", "prUrl6",
                7L, Progress.ON_GOING, LocalDateTime.now().plusDays(5L));
        allieSeed2 = dummyReview(allieTe, seedStu, "review title7", "review content7", "prUrl7",
                2L, Progress.ON_GOING, LocalDateTime.now().plusDays(6L));
        airFafi3 = dummyReview(airTe, fafiStu, "review title8", "review content8", "prUrl8",
                2L, Progress.ON_GOING, LocalDateTime.now().plusDays(7L));
        airSeed3 = dummyReview(airTe, seedStu, "review title9", "review content9", "prUrl9",
                1L, Progress.FINISHED, LocalDateTime.now().plusDays(8L));
        allieFafi2 = dummyReview(allieTe, fafiStu, "review title10", "review content10", "prUrl10",
                4L, Progress.ON_GOING, LocalDateTime.now().plusDays(9L));
        allieSeed3 = dummyReview(allieTe, seedStu, "review title11", "review content11", "prUrl11",
                3L, Progress.ON_GOING, LocalDateTime.now().plusDays(10L));

        em.persist(airSeed1);
        em.persist(allieSeed1);
        em.persist(airFafi1);
        em.persist(allieFafi1);
        em.persist(airSeed2);
        em.persist(airFafi2);
        em.persist(allieSeed2);
        em.persist(airFafi3);
        em.persist(airSeed3);
        em.persist(allieFafi2);
        em.persist(allieSeed3);
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ?????? ??????")
    class ReviewsAsStudent {

        @Test
        @DisplayName("????????? ???????????? - /reviews/student/{id}?page=0&size=3&sort=createdAt,asc")
        void createdAtAsc() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByStudentId(seedStu.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(2);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airSeed1.getTitle(), allieSeed1.getTitle(), airSeed2.getTitle());
        }

        @Test
        @DisplayName("????????? ???????????? - /reviews/student/{id}?page=0&size=3&sort=createdAt,desc")
        void createdAtDesc() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByStudentId(seedStu.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(2);
            assertThat(results.getContent()).extracting("title")
                                            .contains(allieSeed2.getTitle(), airSeed3.getTitle(), allieSeed3.getTitle());
        }

        @Test
        @DisplayName("?????? ??????????????? ??? ????????? - /reviews/student/{id}?page=2&size=3&sort=createdAt,asc")
        void emptyPage() {
            // given
            Pageable pageable = PageRequest.of(2, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByStudentId(seedStu.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(2);
            assertThat(results.getContent()).isEmpty();
        }

        @Test
        @DisplayName("????????? ???????????? ?????? - /reviews/student/{id}?page=0&size=3&name={teacherName}&sort=createdAt,asc")
        void filterTeacherName() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), airTe.getName());

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByStudentId(seedStu.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(1);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airSeed1.getTitle(), airSeed2.getTitle(), airSeed3.getTitle());
        }

        @Test
        @DisplayName("?????? ????????? ?????? - /reviews/student/{id}?page=0&size=3&progress=FINISHED&sort=createdAt,asc")
        void filterProgress() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(Collections.singletonList(Progress.FINISHED), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByStudentId(seedStu.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(1);
            assertThat(results.getContent()).extracting("title")
                                            .contains(allieSeed1.getTitle(), airSeed3.getTitle());
        }

        @Test
        @DisplayName("????????? ?????? & ?????? ????????? ?????? - /reviews/student/{id}?page=0&size=3&name={teacherName}&progress=ON_GOING,FINISHED&sort=createdAt,asc")
        void filterTeacherNameAndProgress() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(
                    Arrays.asList(Progress.ON_GOING, Progress.FINISHED),
                    airTe.getName());

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByStudentId(seedStu.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(1);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airSeed1.getTitle(), airSeed2.getTitle(), airSeed3.getTitle());
        }

    }

    @Nested
    @DisplayName("?????? ??? ?????? ?????? ??????")
    class ReviewsAsTeacher {

        @Test
        @DisplayName("????????? ???????????? - /reviews/teacher/{id}?page=0&size=2&sort=createdAt,asc")
        void createdAtAsc() {
            // given
            Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByTeacherId(airTe.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(3);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airSeed1.getTitle(), airFafi1.getTitle());
        }

        @Test
        @DisplayName("????????? ???????????? - /reviews/teacher/{id}?page=0&size=2&sort=createdAt,desc")
        void createdAtDesc() {
            // given
            Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByTeacherId(airTe.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(3);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airFafi3.getTitle(), airSeed3.getTitle());
        }

        @Test
        @DisplayName("?????? ??????????????? ??? ????????? - /reviews/teacher/{id}?page=2&size=3&sort=createdAt,asc")
        void emptyPage() {
            // given
            Pageable pageable = PageRequest.of(2, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByTeacherId(airTe.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(2);
            assertThat(results.getContent()).isEmpty();
        }

        @Test
        @DisplayName("?????? ???????????? ?????? - /reviews/teacher/{id}?page=0&size=3&name={studentName}&sort=createdAt,asc")
        void filterStudentName() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(new ArrayList<>(), fafiStu.getName());

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByTeacherId(airTe.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(1);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airFafi1.getTitle(), airFafi2.getTitle(), airFafi3.getTitle());
        }

        @Test
        @DisplayName("?????? ????????? ?????? - /reviews/teacher/{id}?page=0&size=3&progress=ON_GOING,FINISHED&sort=createdAt,asc")
        void filterProgress() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(
                    Arrays.asList(Progress.ON_GOING, Progress.FINISHED),
                    null);

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByTeacherId(airTe.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(2);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airSeed1.getTitle(), airFafi1.getTitle(), airSeed2.getTitle());
        }

        @Test
        @DisplayName("?????? ?????? & ?????? ????????? ?????? - /reviews/teacher/{id}?page=0&size=3&name={studentName}&progress=ON_GOING,FINISHED&sort=createdAt,asc")
        void filterStudentNameAndProgress() {
            // given
            Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));
            ReviewSearchCondition condition = new ReviewSearchCondition(
                    Arrays.asList(Progress.ON_GOING, Progress.FINISHED),
                    seedStu.getName());

            // when
            Page<ReviewSummary> results = reviewRepository.searchPageByTeacherId(airTe.getId(), condition, pageable);

            // then
            assertThat(results.getTotalPages()).isEqualTo(1);
            assertThat(results.getContent()).extracting("title")
                                            .contains(airSeed1.getTitle(), airSeed2.getTitle(), airSeed3.getTitle());
        }

    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void reviewDetail() {
        // given
        Long reviewId = 1L;

        // when
        Optional<ReviewSummary> result = reviewRepository.findByReviewId(reviewId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).extracting("title").isEqualTo("review title1");
        assertThat(result.get()).extracting("content").isEqualTo("review content1");
        assertThat(result.get()).extracting("prUrl").isEqualTo("prUrl1");
    }
}

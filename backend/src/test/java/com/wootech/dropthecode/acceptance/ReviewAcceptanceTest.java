package com.wootech.dropthecode.acceptance;

import com.wootech.dropthecode.domain.Progress;
import com.wootech.dropthecode.dto.request.FeedbackRequest;
import com.wootech.dropthecode.dto.request.ReviewRequest;
import com.wootech.dropthecode.dto.response.LoginResponse;
import com.wootech.dropthecode.dto.response.ReviewResponse;
import com.wootech.dropthecode.dto.response.ReviewsResponse;
import com.wootech.dropthecode.exception.ErrorResponse;

import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@DisplayName("리뷰 관련 인수 테스트")
class ReviewAcceptanceTest {

    @Nested
    @DisplayName("리뷰 생성")
    class CreateReview extends AcceptanceTest {
        private LoginResponse student;
        private LoginResponse teacher;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            student = 학생_로그인되어_있음("air");
            teacher = 리뷰어_로그인되어_있음("curry");
        }

        @Test
        @DisplayName("리뷰 요청 성공")
        void createReviewSuccess() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotNull();
        }

        @Test
        @DisplayName("student id가 빈 경우")
        void emptyStudentId() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터(null, teacher.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("리뷰를 요청한 학생이 존재하지 않는 학생인 경우")
        void notExistStudentId() {
            // given
            Long notExistStudentId = 100L;
            ReviewRequest reviewRequest = 리뷰_요청_데이터(notExistStudentId, teacher.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(teacher.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("teacher id가 빈 경우")
        void emptyTeacherId() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), null);

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("리뷰를 요청받은 선생님이 존재하지 않는 선생님인 경우")
        void notExistTeacherId() {
            // given
            Long notExistTeacherId = 100L;
            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), notExistTeacherId);

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("리뷰 요청 제목인 빈 경우")
        void emptyTitle() {
            // given
            ReviewRequest reviewRequest = ReviewRequest.builder()
                                                       .studentId(student.getId())
                                                       .teacherId(teacher.getId())
                                                       .title(null)
                                                       .content("초보라 맞게 한지 잘 모르겠네요.. 잘 부탁드려요!")
                                                       .prUrl("https://github.com/woowacourse-teams/2021-drop-the-code/pull/262")
                                                       .build();

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("리뷰 요청 내용이 빈 경우")
        void emptyContent() {
            // given
            ReviewRequest reviewRequest = ReviewRequest.builder()
                                                       .studentId(student.getId())
                                                       .teacherId(teacher.getId())
                                                       .title("리뷰 요청합니다!")
                                                       .content(null)
                                                       .prUrl("https://github.com/woowacourse-teams/2021-drop-the-code/pull/262")
                                                       .build();

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("리뷰 요청 pr링크가 빈 경우")
        void emptyPrUrl() {
            // given
            ReviewRequest reviewRequest = ReviewRequest.builder()
                                                       .studentId(student.getId())
                                                       .teacherId(teacher.getId())
                                                       .title("리뷰 요청합니다!")
                                                       .content("초보라 맞게 한지 잘 모르겠네요.. 잘 부탁드려요!")
                                                       .prUrl(null)
                                                       .build();

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("유효하지 않은 access token")
        void invalidAccessToken() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청("invalid.access.token", reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("학생이 학생에게 리뷰 요청을 보내는 경우")
        void studentToStudent() {
            // given
            LoginResponse student2 = 학생_로그인되어_있음("allie");
            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), student2.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("자기 자신에게 리뷰 요청을 보내는 경우")
        void selfRequest() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), student.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("로그인을 한 멤버와 리뷰 요청인이 다른 경우")
        void studentNotSameLoginMember() {
            // given
            LoginResponse anonymous = 학생_로그인되어_있음("allie");
            ReviewRequest reviewRequest = 리뷰_요청_데이터(anonymous.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }
    }

    @Nested
    @DisplayName("내가 받은 리뷰 목록 조회")
    class FindStudentReview extends AcceptanceTest {
        private LoginResponse student;
        private LoginResponse teacher;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            student = 학생_로그인되어_있음("air");
            teacher = 리뷰어_로그인되어_있음("curry");

            ReviewRequest reviewRequest1 = 리뷰_요청_데이터(student.getId(), teacher.getId());
            ReviewRequest reviewRequest2 = 리뷰_요청_데이터2(student.getId(), teacher.getId());

            새로운_리뷰_요청(student.getAccessToken(), reviewRequest1);
            새로운_리뷰_요청(student.getAccessToken(), reviewRequest2);
        }

        @Test
        @DisplayName("내가 받은 리뷰 목록 조회 성공")
        void findStudentReviewSuccess() {
            // when
            ExtractableResponse<Response> response = 내가_받은_리뷰_목록_조회_요청(student.getId(), student.getAccessToken());
            ReviewsResponse result = response.as(ReviewsResponse.class);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(result.getPageCount()).isEqualTo(1);
            assertThat(result.getReviews()).hasSize(2);
        }

        @Test
        @DisplayName("로그인 한 유저 id와 받은 리뷰 목록 조희 유저 id가 다른 경우")
        void showOtherStudentReview() {
            // given
            Long anonymousMemberId = 100L;

            // when
            ExtractableResponse<Response> response = 내가_받은_리뷰_목록_조회_요청(anonymousMemberId, student.getAccessToken());
            ErrorResponse result = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(result.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("유효하지 않은 access token")
        void invalidAccessToken() {
            // when
            ExtractableResponse<Response> response = 내가_받은_리뷰_목록_조회_요청(student.getId(), student.getAccessToken() + "invalid");
            ErrorResponse result = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(result.getErrorMessage()).isNotNull();
        }
    }

    @Nested
    @DisplayName("내가 리뷰한 리뷰 목록 조회")
    class FindTeacherReview extends AcceptanceTest {
        private LoginResponse student;
        private LoginResponse teacher;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            student = 학생_로그인되어_있음("air");
            teacher = 리뷰어_로그인되어_있음("curry");

            ReviewRequest reviewRequest1 = 리뷰_요청_데이터(student.getId(), teacher.getId());
            ReviewRequest reviewRequest2 = 리뷰_요청_데이터2(student.getId(), teacher.getId());

            새로운_리뷰_요청(student.getAccessToken(), reviewRequest1);
            새로운_리뷰_요청(student.getAccessToken(), reviewRequest2);
        }

        @Test
        @DisplayName("내가 리뷰한 리뷰 목록 조회 성공")
        void findTeacherReviewSuccess() {
            // when
            ExtractableResponse<Response> response = 내가_맡은_리뷰_목록_조회_요청(teacher.getId());
            ReviewsResponse result = response.as(ReviewsResponse.class);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(result.getPageCount()).isEqualTo(1);
            assertThat(result.getReviews()).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 선생님인 경우")
        void notExistTeacher() {
            // given
            Long notExistTeacherId = 100L;

            // when
            ExtractableResponse<Response> response = 내가_맡은_리뷰_목록_조회_요청(notExistTeacherId);
            ReviewsResponse result = response.as(ReviewsResponse.class);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(result.getPageCount()).isZero();
            assertThat(result.getReviews()).isEmpty();
        }
    }

    @Nested
    @DisplayName("리뷰 목록 조회")
    class FindReview extends AcceptanceTest {
        private String reviewId;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            LoginResponse student = 학생_로그인되어_있음("air");
            LoginResponse teacher = 리뷰어_로그인되어_있음("curry");

            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), teacher.getId());

            reviewId = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest).header("Location").substring(9);
        }

        @Test
        @DisplayName("리뷰 상세 목록 조회 성공")
        void findReviewByIdSuccess() {
            // when
            ExtractableResponse<Response> response = 리뷰_상세_조회_요청(reviewId);
            ReviewResponse result = response.as(ReviewResponse.class);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(result).usingRecursiveComparison()
                              .ignoringFields("id", "createdAt", "teacherProfile", "studentProfile")
                              .isEqualTo(ReviewResponse.builder()
                                                       .title("리뷰 요청합니다!")
                                                       .content("초보라 맞게 한지 잘 모르겠네요.. 잘 부탁드려요!")
                                                       .prUrl("https://github.com/woowacourse-teams/2021-drop-the-code/pull/262")
                                                       .progress(Progress.PENDING)
                                                       .build());
            assertThat(result.getTeacherProfile()).isNotNull();
            assertThat(result.getStudentProfile()).isNotNull();
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 리뷰 상세 목록 조회")
        void findReviewByNotExistId() {
            // given
            String notExistReviewId = "100";

            // when
            ExtractableResponse<Response> response = 리뷰_상세_조회_요청(notExistReviewId);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }
    }

    @Nested
    @DisplayName("리뷰 수정")
    class UpdateReview extends AcceptanceTest {
        private LoginResponse student;
        private LoginResponse teacher;
        private String reviewId;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            student = 학생_로그인되어_있음("air");
            teacher = 리뷰어_로그인되어_있음("curry");

            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), teacher.getId());
            reviewId = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest).header("Location").substring(9);
        }

        @Test
        @DisplayName("리뷰 수정 성공")
        void updateReviewSuccess() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터2(student.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 리뷰_수정_요청(reviewId, student.getAccessToken(), reviewRequest);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("존재하지 않는 리뷰인 경우")
        void notExistReview() {
            // given
            String notExistReviewId = "100";
            ReviewRequest reviewRequest = 리뷰_요청_데이터2(student.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 리뷰_수정_요청(notExistReviewId, student.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }


        @Test
        @DisplayName("로그인 한 멤버와 리뷰의 student가 같지 않은 경우")
        void noAuthorization() {
            // given
            LoginResponse otherStudent = 학생_로그인되어_있음("allie");
            ReviewRequest reviewRequest = 리뷰_요청_데이터2(student.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 리뷰_수정_요청(reviewId, otherStudent.getAccessToken(), reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("유효하지 않은 access token")
        void invalidAccessToken() {
            // given
            ReviewRequest reviewRequest = 리뷰_요청_데이터2(student.getId(), teacher.getId());

            // when
            ExtractableResponse<Response> response = 리뷰_수정_요청(reviewId, "invalid.access.token", reviewRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Pending 상태 리뷰 요청 취소")
    class CancelPendingReview extends AcceptanceTest {
        private LoginResponse student;
        private LoginResponse teacher;
        private String reviewId;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            student = 학생_로그인되어_있음("air");
            teacher = 리뷰어_로그인되어_있음("curry");

            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), teacher.getId());
            reviewId = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest).header("Location").substring(9);
        }

        @Test
        @DisplayName("Pending 상태의 리뷰 취소")
        void pendingReview() {
            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, student.getAccessToken());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("DENIED인 경우")
        void deniedStatus() {
            // given
            리뷰_거절_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ON_GOING인 경우")
        void onGoingStatus() {
            // given
            리뷰_수락_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }


        @Test
        @DisplayName("TEACHER_COMPLETED인 경우")
        void teacherCompletedStatus() {
            // given
            리뷰_수락_요청(reviewId, teacher.getAccessToken());
            선생님_리뷰_완료_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("FINISHED인 경우")
        void finishedStatus() {
            // given
            FeedbackRequest feedbackRequest = new FeedbackRequest(5, "너무 감사합니다!");
            리뷰_수락_요청(reviewId, teacher.getAccessToken());
            선생님_리뷰_완료_요청(reviewId, teacher.getAccessToken());
            학생_리뷰_완료_요청(reviewId, student.getAccessToken(), feedbackRequest);

            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("로그인 한 멤버와 리뷰의 student가 같지 않은 경우")
        void noAuthorization() {
            // given
            LoginResponse otherStudent = 학생_로그인되어_있음("allie");

            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, otherStudent.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 리뷰인 경우")
        void notExistReview() {
            // given
            String notExistReviewId = "100";

            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(notExistReviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("유효하지 않은 access token")
        void invalidAccessToken() {
            // when
            ExtractableResponse<Response> response = 리뷰_취소_요청(reviewId, "invalid.access.token");
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }
    }

    @Nested
    @DisplayName("리뷰 상태 변경")
    class ChangeReviewProgress extends AcceptanceTest {
        private LoginResponse student;
        private LoginResponse teacher;
        private String reviewId;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();
            // given
            student = 학생_로그인되어_있음("air");
            teacher = 리뷰어_로그인되어_있음("curry");

            ReviewRequest reviewRequest = 리뷰_요청_데이터(student.getId(), teacher.getId());
            reviewId = 새로운_리뷰_요청(student.getAccessToken(), reviewRequest).header("Location").substring(9);
        }

        @Test
        @DisplayName("PENDING 상태의 리뷰를 DENIED 상태로 변경 성공")
        void pendingToDenied() {
            // when
            ExtractableResponse<Response> response = 리뷰_거절_요청(reviewId, teacher.getAccessToken());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("PENDING 상태의 리뷰를 ON_GOING 상태로 변경 성공")
        void pendingToOnGoing() {
            // when
            ExtractableResponse<Response> response = 리뷰_수락_요청(reviewId, teacher.getAccessToken());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("ON_GOING 상태의 리뷰를 TEACHER_COMPLETE 상태로 변경 성공")
        void onGoingToTeacherComplete() {
            // given
            리뷰_수락_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 선생님_리뷰_완료_요청(reviewId, teacher.getAccessToken());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("TEACHER_COMPLETE 상태의 리뷰를 FINISHED 상태로 변경 성공")
        void TeacherCompleteToFinished() {
            // given
            FeedbackRequest feedbackRequest = new FeedbackRequest(5, "많이 배웠습니다!");
            리뷰_수락_요청(reviewId, teacher.getAccessToken());
            선생님_리뷰_완료_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 학생_리뷰_완료_요청(reviewId, student.getAccessToken(), feedbackRequest);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("선생님이 아닌데 PENDING -> DENIED 변경 요청을 하는 경우")
        void noTeacherWhenUpdateToDenied() {
            // when
            ExtractableResponse<Response> response = 리뷰_거절_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("PENDING 상태가 아닌데 PENDING -> DENIED 변경 요청을 하는 경우")
        void noPendingButUpdateToDenied() {
            // given
            리뷰_수락_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 리뷰_거절_요청(reviewId, teacher.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("선생님이 아닌데 PENDING -> ON_GOING 변경 요청을 하는 경우")
        void noTeacherWhenUpdateToOnGoing() {
            // when
            ExtractableResponse<Response> response = 리뷰_수락_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("PENDING 상태가 아닌데 PENDING -> ON_GOING 변경 요청을 하는 경우")
        void noPendingButUpdateToOnGoing() {
            // given
            리뷰_수락_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 리뷰_수락_요청(reviewId, teacher.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("선생님이 아닌데 ON_GOING -> TEACHER_COMPLETE 변경 요청을 하는 경우")
        void noTeacherWhenUpdateToCompleteReview() {
            // given
            리뷰_수락_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 선생님_리뷰_완료_요청(reviewId, student.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ON_GOING 상태가 아닌데 ON_GOING -> TEACHER_COMPLETE 변경 요청을 하는 경우")
        void noOnGoingButUpdateToCompleteReview() {
            // when
            ExtractableResponse<Response> response = 선생님_리뷰_완료_요청(reviewId, teacher.getAccessToken());
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("학생이 아닌데 TEACHER_COMPLETE -> FINISHED 변경 요청을 하는 경우")
        void noStudentWhenUpdateToFinishReview() {
            // given
            FeedbackRequest feedbackRequest = new FeedbackRequest(5, "많이 배웠습니다!");
            리뷰_수락_요청(reviewId, teacher.getAccessToken());
            선생님_리뷰_완료_요청(reviewId, teacher.getAccessToken());

            // when
            ExtractableResponse<Response> response = 학생_리뷰_완료_요청(reviewId, teacher.getAccessToken(), feedbackRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("TEACHER_COMPLETE 상태가 아닌데 TEACHER_COMPLETE -> FINISHED 변경 요청을 하는 경우")
        void noTeacherCompletedButUpdateToFinishReview() {
            // given
            FeedbackRequest feedbackRequest = new FeedbackRequest(5, "많이 배웠습니다!");

            // when
            ExtractableResponse<Response> response = 학생_리뷰_완료_요청(reviewId, student.getAccessToken(), feedbackRequest);
            ErrorResponse error = 예외_결과(response);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(error.getErrorMessage()).isNotNull();
        }
    }

    public static ExtractableResponse<Response> 새로운_리뷰_요청(String accessToken, ReviewRequest request) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .contentType(APPLICATION_JSON_VALUE)
                          .body(request)
                          .when()
                          .post("/reviews")
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 내가_받은_리뷰_목록_조회_요청(Long id, String accessToken) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .when()
                          .get("/reviews/student/{id}", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 내가_맡은_리뷰_목록_조회_요청(Long id) {
        return RestAssured.given()
                          .when()
                          .get("/reviews/teacher/{id}", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 리뷰_상세_조회_요청(String id) {
        return RestAssured.given()
                          .when()
                          .get("/reviews/{id}", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 리뷰_수정_요청(String id, String accessToken, ReviewRequest request) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .contentType(APPLICATION_JSON_VALUE)
                          .body(request)
                          .when()
                          .patch("/reviews/{id}", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 리뷰_취소_요청(String id, String accessToken) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .contentType(APPLICATION_JSON_VALUE)
                          .when()
                          .delete("/reviews/{id}", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 리뷰_거절_요청(String id, String accessToken) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .when()
                          .patch("/reviews/{id}/deny", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 리뷰_수락_요청(String id, String accessToken) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .when()
                          .patch("/reviews/{id}/accept", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 선생님_리뷰_완료_요청(String id, String accessToken) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .when()
                          .patch("/reviews/{id}/complete", id)
                          .then()
                          .extract();
    }

    public static ExtractableResponse<Response> 학생_리뷰_완료_요청(String id, String accessToken, FeedbackRequest request) {
        return RestAssured.given()
                          .header("Authorization", "Bearer " + accessToken)
                          .contentType(APPLICATION_JSON_VALUE)
                          .body(request)
                          .when()
                          .patch("/reviews/{id}/finish", id)
                          .then()
                          .extract();
    }

    public static ReviewRequest 리뷰_요청_데이터(Long studentId, Long teacherId) {
        return ReviewRequest.builder()
                            .studentId(studentId)
                            .teacherId(teacherId)
                            .title("리뷰 요청합니다!")
                            .content("초보라 맞게 한지 잘 모르겠네요.. 잘 부탁드려요!")
                            .prUrl("https://github.com/woowacourse-teams/2021-drop-the-code/pull/262")
                            .build();
    }

    public static ReviewRequest 리뷰_요청_데이터2(Long studentId, Long teacherId) {
        return ReviewRequest.builder()
                            .studentId(studentId)
                            .teacherId(teacherId)
                            .title("코리부!")
                            .content("기대중입니다!")
                            .prUrl("https://github.com/woowacourse-teams/2021-drop-the-code/pull/262")
                            .build();
    }
}

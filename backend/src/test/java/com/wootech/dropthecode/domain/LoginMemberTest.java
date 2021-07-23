package com.wootech.dropthecode.domain;

import com.wootech.dropthecode.exception.AuthorizationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginMemberTest {

    @Test
    @DisplayName("토큰이 잘못된 경우 검증 - 401에러 발생")
    void wrongToken() {
        // given
        LoginMember loginMember = LoginMember.anonymous();

        // when
        // then
        assertThatThrownBy(loginMember::validatesAnonymous)
                .isInstanceOf(AuthorizationException.class);
    }

}
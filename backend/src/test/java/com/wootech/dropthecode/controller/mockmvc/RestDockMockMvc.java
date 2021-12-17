package com.wootech.dropthecode.controller.mockmvc;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static capital.scalable.restdocs.misc.AuthorizationSnippet.documentAuthorization;
import static com.wootech.dropthecode.controller.ControllerTest.ACCESS_TOKEN;
import static com.wootech.dropthecode.controller.ControllerTest.BEARER;

public class RestDockMockMvc extends AbstractWebMockMvc {

    private static final String TOKEN_REQUIRED = "User jwt token required.";

    protected RestDockMockMvc(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Override
    public RequestPostProcessor userToken() {
        return request -> {
            request.addHeader("Authorization", BEARER + " " + ACCESS_TOKEN);
            return documentAuthorization(request, TOKEN_REQUIRED);
        };
    }
}

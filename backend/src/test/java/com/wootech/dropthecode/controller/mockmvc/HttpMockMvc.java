package com.wootech.dropthecode.controller.mockmvc;

import java.util.Objects;

import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static com.wootech.dropthecode.controller.ControllerTest.ACCESS_TOKEN;
import static com.wootech.dropthecode.controller.ControllerTest.BEARER;

public class HttpMockMvc extends AbstractWebMockMvc {

    private static HttpMockMvc httpMockMvc;

    protected HttpMockMvc(MockMvc mockMvc) {
        super(mockMvc);
    }

    public static HttpMockMvc of(ApplicationContext applicationContext) {
        if (Objects.isNull(httpMockMvc)) {
            httpMockMvc = new HttpMockMvc(defaultMockMvcBuilder(applicationContext).build());
        }

        return httpMockMvc;
    }

    @Override
    public RequestPostProcessor userToken() {
        return request -> {
            request.addHeader("Authorization", BEARER + " " + ACCESS_TOKEN);
            return request;
        };
    }
}

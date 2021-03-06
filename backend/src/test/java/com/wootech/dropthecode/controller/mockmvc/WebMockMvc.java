package com.wootech.dropthecode.controller.mockmvc;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public interface WebMockMvc {
    ResultActions perform(RequestBuilder requestBuilder) throws Exception;

    RequestPostProcessor userToken();
}

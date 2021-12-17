package com.wootech.dropthecode.controller.mockmvc;

import com.wootech.dropthecode.controller.auth.AuthenticationInterceptor;
import com.wootech.dropthecode.controller.auth.GetAuthenticationInterceptor;
import com.wootech.dropthecode.exception.GlobalExceptionHandler;
import com.wootech.dropthecode.service.AuthService;

import org.springframework.context.ApplicationContext;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.filter.CharacterEncodingFilter;

public abstract class AbstractWebMockMvc implements WebMockMvc {

    protected final MockMvc mockMvc;

    protected AbstractWebMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Override
    public final ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder);
    }

    public static StandaloneMockMvcBuilder defaultMockMvcBuilder(ApplicationContext applicationContext) {
        Object[] controllers = findControllers(applicationContext);

        return MockMvcBuilders.standaloneSetup(controllers)
                              .addFilters(new CharacterEncodingFilter("UTF-8", true))
                              .setControllerAdvice(new GlobalExceptionHandler())
                              .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                              .addInterceptors(
                                      new GetAuthenticationInterceptor(applicationContext.getBean(AuthService.class)),
                                      new AuthenticationInterceptor(applicationContext.getBean(AuthService.class)));
    }

    private static Object[] findControllers(ApplicationContext applicationContext) {
        return applicationContext.getBeansWithAnnotation(Controller.class)
                                 .values()
                                 .toArray();
    }
}

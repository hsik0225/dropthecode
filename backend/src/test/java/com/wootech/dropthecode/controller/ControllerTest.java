package com.wootech.dropthecode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootech.dropthecode.controller.mockmvc.HttpMockMvc;
import com.wootech.dropthecode.controller.mockmvc.RestDocsMockMvcFactory;
import com.wootech.dropthecode.controller.mockmvc.WebMockMvc;
import com.wootech.dropthecode.service.*;
import com.wootech.dropthecode.service.chat.RedisPublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@WebMvcTest(excludeAutoConfiguration = AutoConfigureMockMvc.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class ControllerTest {

    public static final String CODE = "XfWjrPh7lFumDNFpd3K5";
    public static final String GITHUB = "github";
    public static final String NAME = "air";
    public static final String EMAIL = "air.junseo@gmail.com";
    public static final String IMAGE_URL = "https://ssl.pstatic.net/static/pwe/address/img_profile.png";
    public static final String GITHUB_URL = "https://github.com/";
    public static final String STUDENT_ROLE = "STUDENT";
    public static final String BEARER = "Bearer";
    public static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    public static final String NEW_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNjI2OTIyNjkyLCJleHAiOjE2MjY5MjYyOTJ9.dsb5uqMS__VcYToB8QrQFVGOkONeDtMyMv4tMXTUuhY";
    public static final String CHATTING_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaWF0IjoxNjM1MzI3MDA3LCJleHAiOjE2MzUzMjczMDd9.PDj0IxSHckHoqBaoGzacsjE3UgAtG4Qjhm619I5xmyk";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    protected ObjectMapper objectMapper;

    protected WebMockMvc successMockMvc;

    protected WebMockMvc failMockMvc;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider provider) {
        if (needDocumentation()) {
            this.successMockMvc = RestDocsMockMvcFactory.successRestDocsMockMvc(provider, applicationContext);
            this.failMockMvc = RestDocsMockMvcFactory.failRestDocsMockMvc(provider, applicationContext);
            return;
        }

        this.successMockMvc = HttpMockMvc.of(applicationContext);
        this.failMockMvc = HttpMockMvc.of(applicationContext);
    }

    private boolean needDocumentation() {
        return environment.getProperty("documentation-required", "false").equals("true");
    }

    @MockBean
    protected OauthService oauthService;
    @MockBean
    protected AuthService authService;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected TeacherService teacherService;
    @MockBean
    protected LanguageService languageService;
    @MockBean
    protected ReviewService reviewService;
    @MockBean
    protected FeedbackService feedbackService;
    @MockBean
    protected RoomService roomService;
    @MockBean
    protected ChattingService chattingService;
    @MockBean
    protected NotificationService notificationService;
    @MockBean
    protected RedisPublisher redisPublisher;
}

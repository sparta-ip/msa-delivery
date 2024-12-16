package com.msa_delivery.user.presentation.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa_delivery.user.application.dtos.UserSearchDto;
import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.msa_delivery.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * TODO : 테스트 전에는 eureka 서버와 gateway 서버가 실행 된 상태여야 합니다.
 * 반드시 위 2개의 서버가 실행된 상태인지 확인해주세요.
 */
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    @Rollback
    void setUp() {
        User user = User.builder()
                .username("master001")
                .password(passwordEncoder.encode("aA123123!"))
                .role(UserRoleEnum.MASTER)
                .slackId("slackMaster01")
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
        userRepository.save(user);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetGetUserSuccess() throws Exception {
        //given
        User user = userRepository.findByUsername("master001").orElseThrow();

        //when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/users/{userId}", user.getUserId())
                                .header("X-User_Id", user.getUserId().toString())
                                .header("X-Username", user.getUsername())
                                .header("X-Role", user.getRole().toString())
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                //then
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "단건 조회 성공",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("USER-SERVICE V1")
                                                .summary("단건 조회")
                                                .description(
                                                        """
                                                                ## USER 서비스 단건조회 엔드포인트입니다.
                                                                ### 권한이 필요하며 gateway를 통해 jwt를 보내주세요.
                                                                
                                                                ---
                                                                
                                                                - 단건 조회에 필요한 userId를 url에 입력해주세요.
                                                                """)
                                                .pathParameters(
                                                        parameterWithName("userId").description("아이디")
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("사용자 이름"),
                                                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할"),
                                                        fieldWithPath("data.slackId").type(JsonFieldType.STRING).description("슬랙 ID"),
                                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                                                        fieldWithPath("data.createdBy").type(JsonFieldType.STRING).description("생성자"),
                                                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                                                        fieldWithPath("data.updatedBy").type(JsonFieldType.STRING).description("수정자")
                                                )
                                                .build()
                                )
                        )
                );
    }

    @Test
    @Transactional
    @Rollback
    public void testGetSearchUsers() throws Exception {
        //given
        User user = userRepository.findByUsername("master001").orElseThrow();

        UserSearchDto userSearchDto = UserSearchDto.builder().build();
        String searchDtoToJson = objectMapper.writeValueAsString(userSearchDto);

        //when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/users")
                                .header("X-User_Id", user.getUserId().toString())
                                .header("X-Username", user.getUsername())
                                .header("X-Role", user.getRole().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(searchDtoToJson)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                //then
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "search 성공",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("USER-SERVICE V1")
                                                .summary("유저 search")
                                                .description(
                                                        """
                                                                ## USER search 엔드포인트입니다.
                                                                ### 권한이 필요하며 gateway를 통해 jwt를 보내주세요.
                                                                
                                                                ---
                                                                
                                                                - 검색에 필요한 값을 쿼리파라미터에 작성해주세요.
                                                                """)
                                                .requestFields(
                                                        fieldWithPath("username")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("검색할 사용자 이름 (옵션)"),
                                                        fieldWithPath("role")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("사용자의 역할 (예: MASTER, COMPANY_MANAGER 등)"),
                                                        fieldWithPath("slackId")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("사용자 슬랙 ID (옵션)"),
                                                        fieldWithPath("createdAtStart")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("조회 시작 날짜 (형식: yyyy-MM-dd'T'HH:mm:ss)"),
                                                        fieldWithPath("createdAtEnd")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("조회 종료 날짜 (형식: yyyy-MM-dd'T'HH:mm:ss)"),
                                                        fieldWithPath("updatedAtStart")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("수정 시작 날짜 (형식: yyyy-MM-dd'T'HH:mm:ss)"),
                                                        fieldWithPath("updatedAtEnd")
                                                                .type(JsonFieldType.STRING)
                                                                .optional()
                                                                .description("수정 종료 날짜 (형식: yyyy-MM-dd'T'HH:mm:ss)"),
                                                        fieldWithPath("isDeleted")
                                                                .type(JsonFieldType.BOOLEAN)
                                                                .optional()
                                                                .description("삭제된 사용자 여부 (기본값: false)"),
                                                        fieldWithPath("page")
                                                                .type(JsonFieldType.NUMBER)
                                                                .optional()
                                                                .description("페이지 번호 (기본값: 1)"),
                                                        fieldWithPath("size")
                                                                .type(JsonFieldType.NUMBER)
                                                                .optional()
                                                                .description("페이지 크기 (기본값: 20)"),
                                                        fieldWithPath("sort")
                                                                .type(JsonFieldType.ARRAY)
                                                                .optional()
                                                                .description("정렬할 필드 배열 (예: username, createdAt 등)"),
                                                        fieldWithPath("direction")
                                                                .type(JsonFieldType.ARRAY)
                                                                .optional()
                                                                .description("정렬 방향 배열 (예: ASC, DESC 등)")
                                                )

                                                .build()
                                )
                        )
                );
    }


}
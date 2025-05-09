package com.msa_delivery.user.presentation.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.application.dtos.UserRequestDto;
import com.msa_delivery.user.application.dtos.UserSearchDto;
import com.msa_delivery.user.application.service.AuthService;
import com.msa_delivery.user.application.service.DeliveryService;
import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.msa_delivery.user.domain.repository.UserRepository;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
import com.msa_delivery.user.infrastructure.dtos.VerifyUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * TODO : 테스트 전에는 eureka 서버와 gateway 서버가 실행 된 상태여야 합니다.
 * 반드시 위 2개의 서버가 실행된 상태인지 확인해주세요.
 */
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private DeliveryService deliveryService; // FeignClient Mocking

    @MockitoBean
    private AuthService authService;

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
                                "search 성공 케이스",
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
                                                .build()
                                )
                        )
                );
    }

    @Transactional
    @Rollback
    @Test
    public void testPutUpdate() throws Exception {
        //given
        User user = userRepository.findByUsername("master001").orElseThrow();

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .password("123123!aA")
                .slackId("test01")
                .build();

        String requestDtoToJson = objectMapper.writeValueAsString(userRequestDto);

        //when
        when(authService.verifyUser(any(VerifyUserDto.class))).thenReturn(true);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.put("/api/users/{username}", user.getUsername())
                                .header("X-User_Id", user.getUserId().toString())
                                .header("X-Username", user.getUsername())
                                .header("X-Role", user.getRole().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestDtoToJson)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                //then
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "수정 성공",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("USER-SERVICE V1")
                                                .summary("회원 수정")
                                                .description(
                                                        """
                                                                ## User 서비스 회원 수정 엔드포인트입니다.
                                                                
                                                                ---
                                                                
                                                                - 수정에 필요한 정보들을 json 형식으로 입력해주세요.
                                                                - 수정은 MASTER 권한만 가능합니다.      
                                                                """
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("수정된 유저 아이디"),
                                                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저명"),
                                                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 역할"),
                                                        fieldWithPath("data.slackId").type(JsonFieldType.STRING).description("슬랙 아이디"),
                                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                                        fieldWithPath("data.createdBy").type(JsonFieldType.STRING).description("생성자"),
                                                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                                        fieldWithPath("data.updatedBy").type(JsonFieldType.STRING).description("수정자")
                                                )
                                                .build()
                                )
                        )
                );
    }

    @Transactional
    @Rollback
    @Test
    public void testDeleteSoftDeleteUser() throws Exception {
        //given
        User user = userRepository.findByUsername("master001").orElseThrow();

        GetUUIDDto.UUIDListDto mockDeliveryManager = GetUUIDDto.UUIDListDto.builder()
                .deliveryManagerId(1L)
                .build();

        //when
        when(authService.verifyUser(any(VerifyUserDto.class))).thenReturn(true);

        ApiResponseDto<GetUUIDDto> mockResponse = ApiResponseDto.response(HttpStatus.OK.value(), "조회 성공", new GetUUIDDto(List.of(mockDeliveryManager)));
        when(deliveryService.getDeliveryManagerByUserId(any(Long.class), any(String.class), any(String.class), eq(UserRoleEnum.MASTER.toString())))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(mockResponse));

        // Mocking the response from softDeleteDeliveryManager
        List<Long> deliveryManagerIds = mockResponse.getData().getContent().stream()
                .map(GetUUIDDto.UUIDListDto::getDeliveryManagerId)
                .toList();

        for (Long deliveryManagerId : deliveryManagerIds) {
            when(deliveryService.softDeleteDeliveryManager(eq(deliveryManagerId), any(String.class), any(String.class), eq(UserRoleEnum.MASTER.toString())))
                    .thenReturn(ResponseEntity.ok(ApiResponseDto.response(HttpStatus.OK.value(), "삭제 성공", null)));
        }

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/api/users/{username}", user.getUsername())
                                .header("X-User_Id", user.getUserId().toString())
                                .header("X-Username", user.getUsername())
                                .header("X-Role", user.getRole().toString())
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                //then
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "삭제 성공",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("USER-SERVICE V1")
                                                .summary("회원 삭제")
                                                .description(
                                                        """
                                                                ## User 서비스 회원 소프트 삭제 엔드포인트입니다.
                                                                
                                                                ---
                                                                
                                                                - 소프트 삭제는 MASTER 권한만 가능합니다.
                                                                - delivery 서버로 delivery manager 삭제도 함께 요청합니다.
                                                                """
                                                )

                                                .build()
                                )
                        )
                );

    }

}
package com.msa_delivery.auth.presentation.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;


@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    @Rollback(value = true)
    public void testPostSignUpSuccess() throws Exception {
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
                .username("master000")
                .password("aA123123!")
                .role(UserRoleEnum.MASTER)
                .slackId("slackMaster01")
                .build();

        String requestDtoToJson = objectMapper.writeValueAsString(authRequestDto);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/auth/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestDtoToJson)
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated()
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "회원 가입 성공",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("AUTH-SERVICE V1")
                                                .summary("회원가입")
                                                .description(
                                                        """
                                                                ## Auth 서비스 회원가입 엔드포인트입니다.
                                                                
                                                                ---
                                                                
                                                                - 회원가입에 필요한 정보들을 json 형식으로 입력해주세요.
                                                                """)
                                                .requestFields(
                                                        fieldWithPath("username").type(JsonFieldType.STRING).description("유저명(최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9))"),
                                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호(최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자)"),
                                                        fieldWithPath("role").type(JsonFieldType.STRING).description("유저 역할(MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER)"),
                                                        fieldWithPath("slack_id").type(JsonFieldType.STRING).description("슬랙 아이디")
                                                )
                                                .build()
                                )
                        )
                )
        ;
    }
}
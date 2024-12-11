package com.msa_delivery.user.presentation.controller;

import com.msa_delivery.user.application.dtos.UserSearchDto;
import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<UserDetailResponseDto>>> searchUsers(@ModelAttribute UserSearchDto userSearchDto,
                                                                                   @RequestHeader(value = "X-User_Id", required = true) String userId,
                                                                                   @RequestHeader(value = "X-Username", required = true) String username,
                                                                                   @RequestHeader(value = "X-Role", required = true) String role) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.response(HttpStatus.OK.value(),
                        "조회에 성공하였습니다.",
                        userService.searchUsers(userSearchDto, userId, role)));
    }

}

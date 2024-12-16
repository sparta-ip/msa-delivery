package com.msa_delivery.user.presentation.controller;

import com.msa_delivery.user.application.dtos.*;
import com.msa_delivery.user.application.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<UserDetailResponseDto>>> searchUsers(@ModelAttribute UserSearchDto userSearchDto,
                                                                                   @RequestHeader(value = "X-User_Id", required = true) @NotBlank String userId,
                                                                                   @RequestHeader(value = "X-Username", required = true) @NotBlank String username,
                                                                                   @RequestHeader(value = "X-Role", required = true) @NotBlank String role) {
        return userService.searchUsers(userSearchDto, userId, role);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUser(@PathVariable Long userId,
                                                                             @RequestHeader(value = "X-User_Id", required = true) @NotBlank String headerUserId,
                                                                             @RequestHeader(value = "X-Username", required = true) @NotBlank String username,
                                                                             @RequestHeader(value = "X-Role", required = true) @NotBlank String role) {
        return userService.getUser(userId, headerUserId, role);
    }

    @PutMapping("/{username}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUser(@Valid @RequestBody UserRequestDto userRequestDto,
                                                                                @PathVariable String username,
                                                                                @RequestHeader(value = "X-User_Id", required = true) @NotBlank String userId,
                                                                                @RequestHeader(value = "X-Username", required = true) @NotBlank String headerUsername,
                                                                                @RequestHeader(value = "X-Role", required = true) @NotBlank String role) {
        return userService.updateUser(userRequestDto, username, userId, headerUsername, role);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> softDeleteUser(@PathVariable String username,
                                                            @RequestHeader(value = "X-User_Id", required = true) @NotBlank String userId,
                                                            @RequestHeader(value = "X-Username", required = true) @NotBlank String headerUsername,
                                                            @RequestHeader(value = "X-Role", required = true) @NotBlank String role) {
        return userService.softDeleteUser(username, userId, headerUsername, role);
    }
}

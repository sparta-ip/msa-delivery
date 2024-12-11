package com.msa_delivery.user.application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequestDto {

    private String username;

    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-={}|;:'\",.<>?]).{8,15}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    private UserRoleEnum role;

    @JsonProperty("slack_id")
    private String slackId;
}

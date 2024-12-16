package com.msa_delivery.auth.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msa_delivery.auth.domain.entity.UserRoleEnum;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequestDto {

    @Size(min = 4, max = 10, message = "Username must be between 4 and 10 characters.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "Username must consist of lowercase letters and digits.")
    private String username;

    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-={}|;:'\",.<>?]).{8,15}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    private UserRoleEnum role;

    @JsonProperty("slack_id")
    private String slackId;

    @JsonProperty("master_key")
    private String masterKey;
}

package com.msa_delivery.user.application.dtos;

import com.msa_delivery.user.domain.entity.UserRoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ModelAttribute로 활용하는 dto 객체는 인자에 null 값이 들어올 수 있기 때문에 primitive 타입으로 해주고
 * @NoArgsConstructor + @Setter 를 사용하거나 @AllArgsConstructor를 사용해서 바인딩 시켜준다.
 * 다만, 초기값이 필요할 경우 @NoArgsConstructor + @Setter를 사용해 해결하는 것이 좋다.
 */
@Data
@NoArgsConstructor
public class UserSearchDto {

    private String username;
    private UserRoleEnum role;
    private String slackId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    private LocalDateTime updatedAtStart;
    private LocalDateTime updatedAtEnd;
    private Boolean isDeleted = false;

    private Integer page = 1;
    private Integer size = 20;
    private String[] sort;
    private String[] direction;
}

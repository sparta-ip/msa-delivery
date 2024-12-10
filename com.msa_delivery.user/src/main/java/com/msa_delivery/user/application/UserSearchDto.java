package com.msa_delivery.user.application;

import com.msa_delivery.user.domain.entity.UserRoleEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSearchDto {

    private String username;       // 사용자 이름으로 검색
    private UserRoleEnum role;     // 역할로 검색
    private String slackId;        // Slack ID로 검색
    private LocalDateTime createdAtStart;  // 생성일 시작
    private LocalDateTime createdAtEnd;    // 생성일 종료
    private LocalDateTime updatedAtStart;  // 수정일 시작
    private LocalDateTime updatedAtEnd;    // 수정일 종료
    private boolean isDeleted;     // 삭제 여부로 검색 (true/false)

    private int page;              // 페이지 번호
    private int size;              // 페이지 크기
}

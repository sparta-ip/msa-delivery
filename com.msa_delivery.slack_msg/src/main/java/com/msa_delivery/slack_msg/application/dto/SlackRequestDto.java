package com.msa_delivery.slack_msg.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlackRequestDto {
    private String channel;  // Slack 사용자 ID 또는 채널 ID
    private String text;     // 보낼 메시지 내용
}

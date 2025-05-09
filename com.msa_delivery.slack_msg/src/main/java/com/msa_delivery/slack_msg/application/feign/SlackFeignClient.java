package com.msa_delivery.slack_msg.application.feign;

import com.msa_delivery.slack_msg.application.dto.SlackRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "slack", url = "https://slack.com/api")
public interface SlackFeignClient {

    // Slack API의 chat.postMessage 엔드포인트 호출
    @PostMapping("/chat.postMessage")
    String sendMessageToUser(
        @RequestBody SlackRequestDto request,       // 메시지 데이터를 JSON으로 전달
        @RequestHeader("Authorization") String token    // Authorization 헤더로 슬랙 봇 토큰 전달
    );
}

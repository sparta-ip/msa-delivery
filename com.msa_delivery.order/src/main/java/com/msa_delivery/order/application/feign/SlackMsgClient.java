package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.dto.SlackDataDto;
import com.msa_delivery.order.application.dto.SlackMsgRequestDto;
import com.msa_delivery.order.infrastructure.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack-msg-service", configuration = FeignClientConfig.class)
public interface SlackMsgClient {

    @PostMapping("/api/slack/messages")
    ResponseDto<SlackDataDto> createSlackMsg(@RequestBody SlackMsgRequestDto slackMsgRequestDto);
}

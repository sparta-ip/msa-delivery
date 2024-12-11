package com.msa_delivery.slack_msg.presentation.controller;

import com.msa_delivery.slack_msg.application.dto.ResponseDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgDataDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgRequestDto;
import com.msa_delivery.slack_msg.application.service.SlackMsgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/slack/messages")
@RequiredArgsConstructor
public class SlackMsgController {

    private final SlackMsgService slackMsgService;

    // 슬랙 메시지 생성
    @PostMapping
    public ResponseDto<SlackMsgDataDto> createSlackMsg(
        @RequestBody SlackMsgRequestDto.From slackMsgRequestDto,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return slackMsgService.createSlackMsg(slackMsgRequestDto);
    }
}

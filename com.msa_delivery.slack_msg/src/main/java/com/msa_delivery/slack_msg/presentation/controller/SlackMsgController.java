package com.msa_delivery.slack_msg.presentation.controller;

import com.msa_delivery.slack_msg.application.dto.ResponseDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgDataDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgRequestDto;
import com.msa_delivery.slack_msg.application.service.SlackMsgService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/slack/messages")
@RequiredArgsConstructor
public class SlackMsgController {

    private final SlackMsgService slackMsgService;

    // 슬랙 메시지 생성
    @PostMapping
    public ResponseDto<SlackMsgDataDto> createSlackMsg(
        @RequestBody SlackMsgRequestDto.Create slackMsgRequestDto,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return slackMsgService.createSlackMsg(slackMsgRequestDto);
    }

    // 슬랙 메시지 수정
    @PutMapping("/{slack_msg_id}")
    public ResponseDto<SlackMsgDataDto> updateSlackMsg(
        @RequestBody SlackMsgRequestDto.Update slackMsgRequestDto,
        @RequestParam UUID slack_msg_id,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return slackMsgService.updateSlackMsg(slack_msg_id, slackMsgRequestDto);
    }

    // 슬랙 메시지 삭제
    @DeleteMapping("/{slack_msg_id}")
    public ResponseDto<SlackMsgDataDto> deleteSlackMsg(
        @RequestParam UUID slack_msg_id,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return slackMsgService.deleteSlackMsg(slack_msg_id, username);
    }

    // 슬랙 메시지 조회
    @GetMapping("/{slack_msg_id}")
    public ResponseDto<SlackMsgDataDto> getSlackMsg(
        @RequestParam UUID slack_msg_id,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return slackMsgService.getSlackMsg(slack_msg_id);
    }

    // 슬랙 메시지 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<ResponseDto<?>> getSlackMsgs(
        @RequestParam(defaultValue = "1") int page_number,
        @RequestParam(defaultValue = "10") int page_size,
        @RequestParam(defaultValue = "created_at") String sort_by,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String search,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return ResponseEntity.ok(
            slackMsgService.getSlackMsgs(page_number - 1, page_size, sort_by, direction, search));
    }
}

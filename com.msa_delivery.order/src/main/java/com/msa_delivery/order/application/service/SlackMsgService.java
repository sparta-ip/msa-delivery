package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.SlackMsgRequestDto;
import com.msa_delivery.order.application.feign.SlackMsgClient;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMsgService {

    private final SlackMsgClient slackMsgClient;

    public void createSlackMsg(Long receiver_id, String receiver_slack_id,
        LocalDateTime finalDeliveryDateTime, String msg) {

        SlackMsgRequestDto slackMsgRequestDto = new SlackMsgRequestDto(receiver_id,
            receiver_slack_id, msg, finalDeliveryDateTime);

        log.info("slackMsgRequestDto: {}", slackMsgRequestDto.toString());

        slackMsgClient.createSlackMsg(slackMsgRequestDto);
    }
}

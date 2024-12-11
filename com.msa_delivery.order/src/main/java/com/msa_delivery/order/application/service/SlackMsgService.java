package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.SlackMsgRequestDto;
import com.msa_delivery.order.application.feign.SlackMsgClient;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackMsgService {

    private final SlackMsgClient slackMsgClient;

    public void createSlackMsg(LocalDateTime finalDeliveryDateTime, String msg) {

        SlackMsgRequestDto slackMsgRequestDto = new SlackMsgRequestDto(finalDeliveryDateTime, msg);

        slackMsgClient.createSlackMsg(slackMsgRequestDto);
    }
}

package com.msa_delivery.order.application.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackMsgRequestDto {

    private Long receiver_id;
    private String receiver_slack_id;
    private String msg;
    private LocalDateTime send_time;

}

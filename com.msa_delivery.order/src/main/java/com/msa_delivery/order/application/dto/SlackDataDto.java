package com.msa_delivery.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class SlackDataDto {

    private UUID slack_msg_id;
    private Long receiver_id;
    private String msg;
    private LocalDateTime send_time;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}

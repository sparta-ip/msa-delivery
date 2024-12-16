package com.msa_delivery.slack_msg.application.dto;

import com.msa_delivery.slack_msg.domain.model.SlackMsg;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SlackMsgDataDto {

    private UUID slack_msg_id;
    private Long receiver_id;
    private String msg;
    private LocalDateTime send_time;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;

    public SlackMsgDataDto(SlackMsg slackMsg) {
        this.slack_msg_id = slackMsg.getSlack_msg_id();
        this.receiver_id = slackMsg.getReceiver_id();
        this.msg = slackMsg.getMsg();
        this.send_time = slackMsg.getSend_time();
        this.created_at = slackMsg.getCreated_at();
        this.created_by = slackMsg.getCreated_by();
        this.updated_at = slackMsg.getUpdated_at();
        this.updated_by = slackMsg.getUpdated_by();
    }

}

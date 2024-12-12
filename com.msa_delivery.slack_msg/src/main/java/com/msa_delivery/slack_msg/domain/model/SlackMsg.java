package com.msa_delivery.slack_msg.domain.model;

import com.msa_delivery.slack_msg.application.dto.SlackMsgRequestDto.Create;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_slack_msges", schema = "SLACKMSG")
@Where(clause = "is_deleted = false")
public class SlackMsg extends BaseEntity{

    @Id
    private UUID slack_msg_id;

    @Column(nullable = false)
    private Long receiver_id;

    @Column(nullable = false)
    private String receiver_slack_id;

    @Column(nullable = false)
    private String msg;

    @Column(nullable = false)
    private LocalDateTime send_time;

    public static SlackMsg createSlackMsg(Create slackMsgRequestDto) {

        return SlackMsg.builder()
            .slack_msg_id(UUID.randomUUID())
            .receiver_id(slackMsgRequestDto.getReceiver_id())
            .receiver_slack_id(slackMsgRequestDto.getReceiver_slack_id())
            .msg(slackMsgRequestDto.getMsg())
            .send_time(slackMsgRequestDto.getSend_time())
            .build();
    }

    public void updateSlackMsg(Long receiver_id, String receiver_slack_id, String msg, LocalDateTime send_time) {
        if (receiver_id != null) {
            this.receiver_id = receiver_id;
        }
        if (receiver_slack_id != null && !receiver_slack_id.isEmpty()) {
            this.receiver_slack_id = receiver_slack_id;
        }
        if (msg != null && !msg.isEmpty()) {
            this.msg = msg;
        }
        if (send_time != null) {
            this.send_time = send_time;
        }
    }

}

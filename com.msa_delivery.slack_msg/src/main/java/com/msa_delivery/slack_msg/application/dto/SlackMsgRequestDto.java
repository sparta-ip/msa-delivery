package com.msa_delivery.slack_msg.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public class SlackMsgRequestDto {


    @Getter
    @Builder
    @NoArgsConstructor
    public static class Create {

        @NotNull(message = "수신자 ID를 입력해주세요.")
        private Long receiver_id;

        @NotNull(message = "수신자 슬랙 ID를 입력해주세요.")
        private String receiver_slack_id;

        @NotNull(message = "메시지 내용을 입력해주세요.")
        private String msg;

        @NotNull(message = "최종 발송 시한을 입력해주세요.")
        private LocalDateTime send_time;

        public Create (Long receiver_id, String receiver_slack_id, String msg, LocalDateTime send_time) {
            this.receiver_id = receiver_id;
            this.receiver_slack_id = receiver_slack_id;
            this.msg = msg;
            this.send_time = send_time;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    public static class Update {

        private Long receiver_id;

        private String receiver_slack_id;

        private String msg;

        private LocalDateTime send_time;

        public Update (Long receiver_id,String receiver_slack_id, String msg, LocalDateTime send_time) {
            this.receiver_id = receiver_id;
            this.receiver_slack_id = receiver_slack_id;
            this.msg = msg;
            this.send_time = send_time;
        }
    }
}

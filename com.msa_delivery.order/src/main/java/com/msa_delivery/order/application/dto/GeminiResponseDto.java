package com.msa_delivery.order.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponseDto {

    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }

    // 최종 발송 시한 추출 메서드
    public String getFinalDeliveryTime() {
        if (candidates != null && !candidates.isEmpty()) {
            return candidates.get(0)  // 첫 번째 후보
                .getContent()
                .getParts()
                .get(0)  // 첫 번째 part
                .getText();
        }
        return null;
    }
}

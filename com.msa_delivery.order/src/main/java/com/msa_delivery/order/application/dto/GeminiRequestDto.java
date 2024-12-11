package com.msa_delivery.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestDto {

    private String prompt; // AI에 보낼 자연어 프롬프트

    public static GeminiRequestDto fromOrderData(
        String productInfo,
        String requestDetails,
        String departure,
        String waypoints,
        String destination
    ) {
        String prompt = String.format(
            "당신은 물류 발송 일정 전문가입니다.\n" +
                "아래의 주문 정보를 바탕으로 최종 발송 시한을 계산해 주세요.\n\n" +
                "**주문 정보**\n" +
                "- 상품 및 수량: %s\n" +
                "- 납기 요청 사항: %s\n" +
                "- 발송지: %s\n" +
                "- 경유지: %s\n" +
                "- 도착지: %s\n\n" +
                "**요구 사항**\n" +
                "- 요청된 납기일자 및 시간을 만족하기 위해 최종 발송 시한(마감 시간)을 계산해 주세요.\n" +
                "- 모든 정보를 고려하여, 이 시점까지 발송을 완료해야 하는 시간(최종 발송 시한)을 명시해 주세요.\n" +
                "- 아래의 결과 예시를 보고 예시의 형식에 맞춰서 결과를 보내주세요.\n\n" +
                "**결과 예시**\n" +
                "- 최종 발송 시한: 12월 10일 09:00",
            productInfo, requestDetails, departure, waypoints, destination
        );
        return new GeminiRequestDto(prompt);
    }
}

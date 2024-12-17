package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.GeminiRequestDto;
import com.msa_delivery.order.application.dto.GeminiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geminiClient", url = "https://generativelanguage.googleapis.com/v1beta")
public interface GeminiClient {

    @PostMapping("/models/gemini-1.5-flash-latest:generateContent")
    ResponseEntity<GeminiResponseDto> generateContent(
        @RequestBody GeminiRequestDto requestDto,
        @RequestParam("key") String apiKey
    );
}


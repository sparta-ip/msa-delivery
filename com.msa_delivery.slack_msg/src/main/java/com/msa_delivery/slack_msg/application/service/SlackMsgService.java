package com.msa_delivery.slack_msg.application.service;

import com.msa_delivery.slack_msg.application.dto.ResponseDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgDataDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgRequestDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgRequestDto.Create;
import com.msa_delivery.slack_msg.application.dto.SlackRequestDto;
import com.msa_delivery.slack_msg.application.feign.SlackFeignClient;
import com.msa_delivery.slack_msg.domain.model.SlackMsg;
import com.msa_delivery.slack_msg.domain.repository.SlackMsgRepository;
import com.msa_delivery.slack_msg.domain.repository.SlackMsgRepositoryCustom;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMsgService {

    private final SlackMsgRepository slackMsgRepository;

    private final SlackFeignClient slackFeignClient;

    @Value("${slack.bot.token}")
    private String slackBotToken;

    @Transactional
    public ResponseDto<SlackMsgDataDto> createSlackMsg(SlackMsgRequestDto.From slackMsgRequestDto) {

        try {
            // Slack API로 메시지 전송
            SlackRequestDto slackRequestDto = new SlackRequestDto(
                slackMsgRequestDto.getReceiver_slack_id(),
                slackMsgRequestDto.getMsg()
            );

            String bearerToken = "Bearer " + slackBotToken; // Bearer 형식으로 토큰 전달
            slackFeignClient.sendMessageToUser(slackRequestDto, bearerToken);

            // 슬랙 메시지 생성 및 저장
            SlackMsgRequestDto.Create slackMsgDto = new Create(slackMsgRequestDto.getReceiver_id(),
                slackMsgRequestDto.getMsg(), slackMsgRequestDto.getSend_time());

            SlackMsg slackMsg = SlackMsg.createSlackMsg(slackMsgDto);
            SlackMsg savedSlackMsg = slackMsgRepository.save(slackMsg);

            // 응답 데이터 생성
            SlackMsgDataDto slackMsgDataDto = new SlackMsgDataDto(savedSlackMsg);
            return new ResponseDto<>(HttpStatus.OK.value(), "슬랙 메시지가 생성되었습니다.", slackMsgDataDto);

        } catch (FeignException feignException) {
            log.error("Slack API 호출 중 오류 발생: {}", feignException.getMessage(), feignException);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "슬랙 메시지 전송에 실패했습니다.",
                null);
        } catch (DataAccessException dataAccessException) {
            log.error("데이터베이스 오류 발생: {}", dataAccessException.getMessage(), dataAccessException);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "슬랙 메시지 저장에 실패했습니다.",
                null);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다.",
                null);
        }
    }
}

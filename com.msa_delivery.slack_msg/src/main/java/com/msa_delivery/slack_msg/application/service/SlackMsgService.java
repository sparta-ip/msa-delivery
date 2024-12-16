package com.msa_delivery.slack_msg.application.service;

import com.msa_delivery.slack_msg.application.dto.ResponseDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgDataDto;
import com.msa_delivery.slack_msg.application.dto.SlackMsgRequestDto;
import com.msa_delivery.slack_msg.application.dto.SlackRequestDto;
import com.msa_delivery.slack_msg.application.feign.SlackFeignClient;
import com.msa_delivery.slack_msg.domain.model.SlackMsg;
import com.msa_delivery.slack_msg.domain.repository.SlackMsgRepository;
import com.msa_delivery.slack_msg.domain.repository.SlackMsgRepositoryCustom;
import feign.FeignException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMsgService {

    private final SlackMsgRepository slackMsgRepository;
    private final SlackMsgRepositoryCustom slackMsgRepositoryCustom;

    private final SlackFeignClient slackFeignClient;

    @Value("${slack.bot.token}")
    private String slackBotToken;

    @Transactional
    public ResponseDto<SlackMsgDataDto> createSlackMsg(
        SlackMsgRequestDto.Create slackMsgRequestDto) {

        try {
            // Slack API로 메시지 전송
            SlackRequestDto slackRequestDto = new SlackRequestDto(
                slackMsgRequestDto.getReceiver_slack_id(),
                slackMsgRequestDto.getMsg()
            );

            sendSlackMessage(slackRequestDto);

            // 슬랙 메시지 생성 및 저장
            SlackMsg slackMsg = SlackMsg.createSlackMsg(slackMsgRequestDto);
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

    @Transactional
    public ResponseDto<SlackMsgDataDto> updateSlackMsg(UUID slack_msg_id,
        SlackMsgRequestDto.Update slackMsgRequestDto) {
        try {
            // 메시지 조회
            SlackMsg slackMsg = slackMsgRepository.findById(slack_msg_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Slack 메시지입니다."));

            // receiver_id가 변경되었으나 receiver_slack_id가 없는 경우 예외 던지기
            if (slackMsgRequestDto.getReceiver_id() != null
                && (slackMsgRequestDto.getReceiver_slack_id() == null
                || slackMsgRequestDto.getReceiver_slack_id().isEmpty())) {
                throw new IllegalArgumentException(
                    "receiver_id가 변경되었으나 receiver_slack_id가 누락되었습니다.");
            }

            // Slack 메시지 전송 데이터 생성
            SlackRequestDto slackRequestDto = new SlackRequestDto(
                (slackMsgRequestDto.getReceiver_slack_id() != null)
                    ? slackMsgRequestDto.getReceiver_slack_id() : slackMsg.getReceiver_slack_id(),
                "수정된 사항의 메시지를 다시 보내드립니다.\n\n" +
                    ((slackMsgRequestDto.getMsg() != null) ? slackMsgRequestDto.getMsg()
                        : slackMsg.getMsg())
            );

            // Slack API로 메시지 전송
            sendSlackMessage(slackRequestDto);

            // 기존 slackMsg 수정
            slackMsg.updateSlackMsg(
                slackMsgRequestDto.getReceiver_id(),
                slackMsgRequestDto.getReceiver_slack_id(),
                slackMsgRequestDto.getMsg(),
                slackMsgRequestDto.getSend_time()
            );

            // 응답 데이터 생성
            SlackMsgDataDto slackMsgDataDto = new SlackMsgDataDto(slackMsg);
            return new ResponseDto<>(HttpStatus.OK.value(), "슬랙 메시지가 수정되었습니다.", slackMsgDataDto);

        } catch (FeignException feignException) {
            log.error("Slack API 호출 중 오류 발생: {}", feignException.getMessage(), feignException);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "슬랙 메시지 전송에 실패했습니다.",
                null);
        } catch (DataAccessException dataAccessException) {
            log.error("데이터베이스 오류 발생: {}", dataAccessException.getMessage(), dataAccessException);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "슬랙 메시지 수정에 실패했습니다.",
                null);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다.",
                null);
        }
    }

    @Transactional
    public ResponseDto<SlackMsgDataDto> deleteSlackMsg(UUID slack_msg_id, String username) {
        try {
            // 메시지 조회
            SlackMsg slackMsg = slackMsgRepository.findById(slack_msg_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Slack 메시지입니다."));

            // 소프트 삭제 처리
            slackMsg.delete(username);

            // 응답 데이터 생성
            SlackMsgDataDto slackMsgDataDto = new SlackMsgDataDto(slackMsg);
            return new ResponseDto<>(HttpStatus.OK.value(), "슬랙 메시지가 삭제되었습니다.", slackMsgDataDto);

        } catch (DataAccessException dataAccessException) {
            log.error("데이터베이스 오류 발생: {}", dataAccessException.getMessage(), dataAccessException);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "슬랙 메시지 수정에 실패했습니다.",
                null);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다.",
                null);
        }
    }

    // 슬랙 메시지 조회
    @Transactional(readOnly = true)
    public ResponseDto<SlackMsgDataDto> getSlackMsg(UUID slack_msg_id) {
        try {
            // 메시지 조회
            SlackMsg slackMsg = slackMsgRepository.findById(slack_msg_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Slack 메시지입니다."));

            // 응답 데이터 생성
            SlackMsgDataDto slackMsgDataDto = new SlackMsgDataDto(slackMsg);
            return new ResponseDto<>(HttpStatus.OK.value(), "슬랙 메시지가 조회되었습니다.", slackMsgDataDto);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다.",
                null);
        }
    }

    // 슬랙 메시지 전체 조회 및 검색
    @Transactional(readOnly = true)
    public ResponseDto<Page<SlackMsgDataDto>> getSlackMsgs(int page, int page_size, String sortBy,
        String direction, String search) {
        // page와 page_size가 유효한지 확인
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 0.");
        }
        if (page_size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0.");
        }

        // 페이지네이션 및 정렬 설정
        PageRequest pageRequest = PageRequest.of(
            page,
            page_size,
            Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        // 슬랙메시지 전체 조회 (검색 조건 포함)
        Page<SlackMsg> slackMsgs = slackMsgRepositoryCustom.findSlackMsgsWithSearch(pageRequest,
            search);

        // 결과가 없으면 빈 결과 반환
        if (slackMsgs.getTotalElements() == 0) {
            return new ResponseDto<>(HttpStatus.OK.value(), "검색 결과가 없습니다.", null);
        }

        Page<SlackMsgDataDto> slackMsgDataDtos = new PageImpl<>(slackMsgs.stream()
            .map(slackMsg -> new SlackMsgDataDto(slackMsg))
            .collect(Collectors.toList()), pageRequest, slackMsgs.getTotalElements());

        return new ResponseDto<>(HttpStatus.OK.value(), "조회가 완료되었습니다.", slackMsgDataDtos);
    }

    // 슬랙 메시지 전송
    private void sendSlackMessage(SlackRequestDto slackRequestDto) {
        try {
            String bearerToken = "Bearer " + slackBotToken; // Bearer 형식으로 토큰 전달
            slackFeignClient.sendMessageToUser(slackRequestDto, bearerToken);
        } catch (FeignException feignException) {
            log.error("Slack API 호출 중 오류 발생: {}", feignException.getMessage(), feignException);
            throw feignException;
        }
    }

}

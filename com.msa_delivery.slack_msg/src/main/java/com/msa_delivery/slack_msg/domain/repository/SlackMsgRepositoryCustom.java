package com.msa_delivery.slack_msg.domain.repository;

import com.msa_delivery.slack_msg.domain.model.SlackMsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SlackMsgRepositoryCustom {

    Page<SlackMsg> findSlackMsgsWithSearch(Pageable pageable, String search);
}
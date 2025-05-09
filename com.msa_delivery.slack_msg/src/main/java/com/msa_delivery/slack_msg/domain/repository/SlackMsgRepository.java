package com.msa_delivery.slack_msg.domain.repository;

import com.msa_delivery.slack_msg.domain.model.SlackMsg;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlackMsgRepository extends JpaRepository<SlackMsg, UUID>, SlackMsgRepositoryCustom {

}

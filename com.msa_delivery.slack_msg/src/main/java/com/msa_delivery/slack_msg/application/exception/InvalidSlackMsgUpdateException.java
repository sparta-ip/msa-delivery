package com.msa_delivery.slack_msg.application.exception;

public class InvalidSlackMsgUpdateException extends RuntimeException {

    public InvalidSlackMsgUpdateException(String message) {
        super(message);
    }
}

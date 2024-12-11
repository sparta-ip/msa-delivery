package com.msa_delivery.slack_msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SlackMsgApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlackMsgApplication.class, args);
	}

}

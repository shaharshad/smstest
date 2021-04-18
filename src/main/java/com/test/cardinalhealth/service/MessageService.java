package com.test.cardinalhealth.service;

import com.test.cardinalhealth.dto.MessageDTO;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phoneNumber}")
    private String FROM_NUMBER;

    public void send(MessageDTO messageDTO) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message.creator(new PhoneNumber(messageDTO.getPhoneNumber()), new PhoneNumber(FROM_NUMBER),
                messageDTO.getMessage()).create();
    }
}

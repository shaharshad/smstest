package com.test.cardinalhealth.dto;

import lombok.Data;

/**
 * Message DTO to send sms
 */
@Data
public class MessageDTO {

    private String message;
    private String phoneNumber;
}

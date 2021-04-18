package com.test.cardinalhealth.kafka;

import com.test.cardinalhealth.dto.MessageDTO;
import com.test.cardinalhealth.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageConsumer {

    private static final String TOPIC_NAME = "SMSMessageTopic";
    private static final String RETRY_TOPIC_NAME = TOPIC_NAME + "_Retry";
    private static final String GROUP_ID = "group_id";

    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;;
    private final MessageService messageService;

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID,
            containerFactory = "concurrentKafkaListenerContainerFactory")
    public void sendMessage(MessageDTO messageDTO) {
        log.info("The message is {}", messageDTO);
        try {
            messageService.send(messageDTO);
        } catch (Exception exception) {
            // retry in another topic with _Retry
            kafkaTemplate.send(RETRY_TOPIC_NAME, messageDTO);
        }
    }

    @KafkaListener(topics = RETRY_TOPIC_NAME, groupId = GROUP_ID,
            containerFactory = "concurrentKafkaRetryListenerContainerFactory")
    public void retryMessage(MessageDTO messageDTO) {
        log.info("The retry message is {}", messageDTO);
        messageService.send(messageDTO);
    }
}

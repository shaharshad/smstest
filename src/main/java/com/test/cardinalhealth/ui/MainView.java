package com.test.cardinalhealth.ui;

import com.test.cardinalhealth.dto.MessageDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Route
public class MainView extends VerticalLayout {

    Button send = new Button("Send", VaadinIcon.CHECK.create());
    Button clear = new Button("Clear", VaadinIcon.TRASH.create());

    TextField phoneNumber = new TextField("Phone Number");
    TextArea message = new TextArea("Message");

    Grid<MessageDTO> grid = new Grid<>(MessageDTO.class);
    HorizontalLayout horizontalLayout = new HorizontalLayout(send, clear);
    Binder<MessageDTO> binder = new Binder<>(MessageDTO.class);

    private MessageDTO messageDTO;
    private Set<MessageDTO> messageDTOSet;

    private static final Pattern PATTERN = Pattern.compile("\\+\\d{12}");
    private static final String HEADER = "Message Sender";
    private static final String TOPIC_NAME = "SMSMessageTopic";

    @Autowired
    private KafkaTemplate<String, MessageDTO> kafkaTemplate;

    MainView() {
        add(HEADER);
        add(phoneNumber, message, horizontalLayout, grid);
        binder.bindInstanceFields(this);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        messageDTOSet = new HashSet<>();
        grid.setItems(messageDTOSet);
        clear.getElement().getThemeList().add("error");
        send.getElement().getThemeList().add("primary");
        send.addClickListener(e -> {
            binder.validate();
            if (isFormValid()) {
                sendMessage();
            }
        });
        clear.addClickListener(e -> resetForm());

        binder.forField(message).asRequired("Message must be provided")
                .bind(MessageDTO::getMessage, MessageDTO::setMessage);
        binder.forField(phoneNumber).withValidator(phoneNumber ->
                PATTERN.matcher(phoneNumber).matches(), "Phone Number format must be valid")
                .bind(MessageDTO::getPhoneNumber, MessageDTO::setPhoneNumber);
    }

    private void resetForm() {
        phoneNumber.clear();
        message.clear();
        message.setRequiredIndicatorVisible(false);
        phoneNumber.setRequiredIndicatorVisible(false);
    }

    /**
     * adding message to kafka
     */
    private void sendMessage() {
        messageDTO = new MessageDTO();
        binder.writeBeanIfValid(messageDTO);
        if (!messageDTOSet.add(messageDTO)) {
            Notification.show("Already requested for same message and phone number!", 5000,
                    Notification.Position.TOP_CENTER);
        } else {
            // sending message to kafka topic
            kafkaTemplate.send(TOPIC_NAME, messageDTO);
            resetForm();
            grid.getDataProvider().refreshAll();
        }
    }

    private boolean isFormValid() {
        return !message.isInvalid() && !phoneNumber.isInvalid();
    }
}

package com.example.spring_template.event;

import com.example.spring_template.domain.dto.EmailDTO;
import com.example.spring_template.domain.enums.event.EmailEventType;
import org.springframework.context.ApplicationEvent;

public class EmailEvent extends ApplicationEvent {
    private final EmailEventType emailType;

    public EmailEvent(EmailDTO emailDTO, EmailEventType type) {

        super(emailDTO);
        this.emailType = type;
    }

    public EmailDTO getSource() {
        return (EmailDTO) super.getSource();
    }

    public boolean isMessageEmail() {
        return this.emailType == EmailEventType.MESSAGE;
    }

    public boolean isMultipartEmail() {
        return this.emailType == EmailEventType.MULTIPART_MESSAGE;
    }
}

package com.example.spring_template.event;

import com.example.spring_template.domain.enums.event.PDFEventType;
import com.example.spring_template.event.holder.PDFResultHolder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PDFEvent extends ApplicationEvent {
    private final PDFEventType PDFType;

    @Getter
    private final PDFResultHolder resultHolder;

    public PDFEvent(PDFEventType type, PDFResultHolder resultHolder) {
        super("");
        this.PDFType = type;
        this.resultHolder = resultHolder;
    }

    public String getSource() {
        return (String) super.getSource();
    }

    public boolean isInvoicePDF() {
        return this.PDFType == PDFEventType.INVOICE;
    }
}

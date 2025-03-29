package com.example.spring_template.service.pdf.generator;

import com.example.spring_template.domain.enums.event.ChartEventType;
import com.example.spring_template.event.ChartEvent;
import com.example.spring_template.event.holder.ChartResultHolder;
import com.example.spring_template.service.pdf.base.PDFGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoicePDFGenerator extends PDFGenerator {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public InvoicePDFGenerator(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String parse() {

        Context context = new Context();

        ChartResultHolder resultHolder = new ChartResultHolder();
        this.eventPublisher.publishEvent(new ChartEvent(ChartEventType.BUBBLE, resultHolder));

        context.setVariable("roi_evolution", resultHolder.getBase64Chart());

        context.setVariable("company_name", "My Company, Inc.");
        context.setVariable("company_address", "Bulgaria Plovdiv Centre");
        context.setVariable("company_phone", "(359) 889-356-185");
        context.setVariable("company_email", "info@mycompany.com");

        context.setVariable("invoice_number", "INV-001234");
        context.setVariable("invoice_date", "2025-25-25");
        context.setVariable("due_date", "2025-25-25");
        context.setVariable("payment_terms", "Net 69");

        context.setVariable("client_name", "John Doe");
        context.setVariable("client_address", "456 Client Rd.");
        context.setVariable("client_city_zip", "Client City, ST 78910");
        context.setVariable("ship_to_address", null); // triggers (Same as Bill To)

        context.setVariable("subtotal", "345.00 BGN.");
        context.setVariable("tax", "35.00 BGN.");
        context.setVariable("total", "385.00 BGN.");
        context.setVariable("footer_note", "Pay due 30th of the month");

        List<Map<String, Object>> siteList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> siteData = new HashMap<>();
            siteData.put("address", "Site Address " + (i + 1));
            siteData.put("value", "Value " + (i + 1));

            Map<String, Object> site = new HashMap<>();
            site.put("data", siteData);
            siteList.add(site);
        }
        context.setVariable("site_collection", siteList);

        context.setVariable("has_more_scored_sites", siteList.size() > 3);

        return this.parseTemplate("templates/Invoice.html", context);
    }
}

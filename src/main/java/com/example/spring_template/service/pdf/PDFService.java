package com.example.spring_template.service.pdf;

import com.example.spring_template.event.PDFEvent;
import com.example.spring_template.service.pdf.generator.InvoicePDFGenerator;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PDFService {
    private final InvoicePDFGenerator invoicePDFGenerator;

    @Autowired
    public PDFService(InvoicePDFGenerator invoicePDFGenerator) {
        this.invoicePDFGenerator = invoicePDFGenerator;
    }

    @EventListener(condition = "#event.invoicePDF")
    public byte[] generateInvoicePDF(PDFEvent event) {
        String htmlContent = invoicePDFGenerator.parse();
        byte[] invoice = this.generateReport(htmlContent);

        event.getResultHolder().setPdfResult(invoice);

        return invoice;
    }

    private byte[] generateReport(String content) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        HtmlConverter.convertToPdf(content, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

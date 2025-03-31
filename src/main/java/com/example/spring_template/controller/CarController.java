package com.example.spring_template.controller;

import com.example.spring_template.controller.base.BaseController;
import com.example.spring_template.domain.dto.EmailDTO;
import com.example.spring_template.domain.dto.request.CarRequestDTO;
import com.example.spring_template.domain.dto.response.CarResponseDTO;
import com.example.spring_template.domain.entity.Car;
import com.example.spring_template.domain.enums.event.EmailEventType;
import com.example.spring_template.domain.enums.event.PDFEventType;
import com.example.spring_template.event.EmailEvent;
import com.example.spring_template.event.PDFEvent;
import com.example.spring_template.event.holder.PDFResultHolder;
import com.example.spring_template.service.crud.CarService;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController extends BaseController<Car, CarRequestDTO, CarResponseDTO> {

    private final ApplicationEventPublisher eventPublisher;

    public CarController(CarService service, ApplicationEventPublisher eventPublisher) {
        super(service);
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/invoice-pdf")
    public ResponseEntity<byte[]> generateInvoicePdf() {
        PDFResultHolder resultHolder = new PDFResultHolder();

        // 🔥 Publish the PDF event
        eventPublisher.publishEvent(new PDFEvent(PDFEventType.INVOICE, resultHolder));

        // 📄 Get the generated PDF bytes
        byte[] pdfBytes = resultHolder.getPdfResult();

        if (pdfBytes == null || pdfBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // ✅ Return it as a PDF response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("invoice.pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/contact")
    public ResponseEntity<Void> contactSupport(@Valid @RequestBody EmailDTO emailDTO) {
        eventPublisher.publishEvent(new EmailEvent(emailDTO, EmailEventType.MESSAGE));
        return ResponseEntity.ok().build();
    }
}
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.example.spring_template.constant.Constants.API_ENDPOINT;

@RestController
@RequestMapping(API_ENDPOINT + "/cars")
@Tag(name = "Car API", description = "Operations related to cars and support")
public class CarController extends BaseController<Car, CarRequestDTO, CarResponseDTO> {

    private final ApplicationEventPublisher eventPublisher;

    public CarController(CarService service, ApplicationEventPublisher eventPublisher) {
        super(service);
        this.eventPublisher = eventPublisher;
    }

    @Operation(
        summary = "Generate Invoice PDF",
        description = "Generates an invoice in PDF format and returns it as a downloadable file.",
        responses = {
            @ApiResponse(responseCode = "200", description = "PDF generated successfully",
                content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "500", description = "Failed to generate PDF")
        }
    )
    @GetMapping("/invoice-pdf")
    public ResponseEntity<byte[]> generateInvoicePdf() {
        PDFResultHolder resultHolder = new PDFResultHolder();
        eventPublisher.publishEvent(new PDFEvent(PDFEventType.INVOICE, resultHolder));
        byte[] pdfBytes = resultHolder.getPdfResult();

        if (pdfBytes == null || pdfBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("invoice.pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @Operation(
        summary = "Contact Support",
        description = "Sends an email message to support.",
        requestBody = @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = EmailDTO.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email payload")
        }
    )
    @PostMapping("/contact")
    public ResponseEntity<Void> contactSupport(@Valid @RequestBody EmailDTO emailDTO) {
        eventPublisher.publishEvent(new EmailEvent(emailDTO, EmailEventType.MESSAGE));
        return ResponseEntity.ok().build();
    }
}

package com.example.spring_template.service.mail;

import com.example.spring_template.domain.enums.EmailStatus;
import com.example.spring_template.event.EmailEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    public SmtpService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @EventListener(condition = "#event.messageEmail")
    public EmailStatus sendEmail(EmailEvent event) {
        try{
            // Create the email message
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo("samuildobrinski@gmail.com");
            mailMessage.setSubject(event.getSource().getSubject());

            // Customize the message content
            String body = "Message from " + event.getSource().getName() + "(" + event.getSource().getEmail() + ")!\n\nMessage:\n" + event.getSource().getMessage();
            mailMessage.setText(body);

            javaMailSender.send(mailMessage);
            return EmailStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return EmailStatus.ERROR;
        }
    }

//    public void sendMailWithAttachment(EmailDTO request) throws Exception {
//        // Creating a mime message
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper;
//
//        mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//        mimeMessageHelper.setFrom(sender);
//        mimeMessageHelper.setTo(request.getEmail());
//        mimeMessageHelper.setSubject(request.getSubject());
//
//        String body = "Message from " + request.getName() + "(" + request.getEmail() + ")!\n\nMessage:\n" + request.getMessage();
//        mimeMessageHelper.setText(body);
//
//        byte[] detailedPdfBytes = pdfService.generatePdf(true);
//        byte[] summaryPdfBytes = pdfService.generatePdf(false);
//
//        mimeMessageHelper.addAttachment("summary.pdf", new ByteArrayResource(summaryPdfBytes));
//        mimeMessageHelper.addAttachment("detailed.pdf", new ByteArrayResource(detailedPdfBytes));
//
//        // Sending the mail
//        javaMailSender.send(mimeMessage);
//    }
}

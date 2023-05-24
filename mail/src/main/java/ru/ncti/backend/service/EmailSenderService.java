package ru.ncti.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.ncti.backend.model.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static ru.ncti.backend.rabbitmq.model.RabbitQueue.EMAIL_UPDATE;

@Service
@Slf4j
public class EmailSenderService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailSenderService(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @RabbitListener(queues = EMAIL_UPDATE)
    public void sendEmail(Email email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getProperties());
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        String html = templateEngine.process(email.getTemplate(), context);
        helper.setText(html, true);

        log.info("Sending email: {}", email);
        emailSender.send(message);
    }

}

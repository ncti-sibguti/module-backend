package ru.ncti.modulebackend.impl;

import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import ru.ncti.modulebackend.Email;

import javax.mail.internet.MimeMessage;
import java.util.Map;


@Service
public class EmailServiceImpl {

    private final JavaMailSender javaMailSender;
    private final Configuration fmConfiguration;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender, Configuration fmConfiguration) {
        this.javaMailSender = javaMailSender;
        this.fmConfiguration = fmConfiguration;
    }

    public String sendSimpleMail(Email details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(details.getSubject());
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(getContentFromTemplate(details.getMap()), true);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    private String getContentFromTemplate(Map<String, ?> model) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(FreeMarkerTemplateUtils
                    .processTemplateIntoString(fmConfiguration.getTemplate("email-template.ftl"), model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}

package ru.ncti.modulebackend;

import java.util.Map;

public class Email {

    private String recipient;
    private String subject;
    private String attachment;
    private Map<String, ?> map;

    public Email() {
    }

    public Email(String recipient, String subject, Map<String, ?> map) {
        this.recipient = recipient;
        this.subject = subject;
        this.map = map;
    }

    public Email(String recipient, String subject, String attachment) {
        this.recipient = recipient;
        this.subject = subject;
        this.attachment = attachment;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Map<String, ?> getMap() {
        return map;
    }

    public void setMap(Map<String, ?> map) {
        this.map = map;
    }
}

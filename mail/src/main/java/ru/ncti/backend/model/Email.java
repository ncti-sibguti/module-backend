package ru.ncti.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Email {
    String to;
    String subject;
    String text;
    String template;
    Map<String, Object> properties;
}

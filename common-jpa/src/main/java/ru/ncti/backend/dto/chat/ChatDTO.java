package ru.ncti.backend.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatDTO {
    private String name;
    private List<Long> ids;
}


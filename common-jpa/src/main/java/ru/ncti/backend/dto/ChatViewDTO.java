package ru.ncti.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class ChatViewDTO {
    private UUID id;
    private String name;
    private Integer userCount;
}

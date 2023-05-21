package ru.ncti.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncti.backend.dto.chat.UserFromMessageDTO;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageFromChatDTO {
    private UUID id;
    private String text;
    private UserFromMessageDTO author;
    private Long createdAt;
    private String type;
}

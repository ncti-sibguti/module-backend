package ru.ncti.backend.service;

import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.ncti.backend.dto.ChatDTO;
import ru.ncti.backend.dto.ChatViewDTO;
import ru.ncti.backend.entiny.Chat;
import ru.ncti.backend.entiny.User;
import ru.ncti.backend.repository.ChatRepository;
import ru.ncti.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public String createChatroom(ChatDTO dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Chat chat = new Chat();
        chat.getUsers().add(user);

        if (!dto.getIds().isEmpty()) {
            for (Long id :
                    dto.getIds()) {
                chat.getUsers().add(userRepository.getById(id));
            }
        }

        chat.setName(dto.getName());
        chatRepository.save(chat);
        return "OK";
    }
}


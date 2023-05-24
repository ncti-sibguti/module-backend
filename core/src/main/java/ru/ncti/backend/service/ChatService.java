package ru.ncti.backend.service;

import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ncti.backend.dto.chat.ChatDTO;
import ru.ncti.backend.dto.chat.ChatViewDTO;
import ru.ncti.backend.dto.chat.MessageDTO;
import ru.ncti.backend.dto.chat.MessageFromChatDTO;
import ru.ncti.backend.dto.chat.UserFromMessageDTO;
import ru.ncti.backend.entiny.Chat;
import ru.ncti.backend.entiny.Message;
import ru.ncti.backend.entiny.User;
import ru.ncti.backend.repository.ChatRepository;
import ru.ncti.backend.repository.MessageRepository;
import ru.ncti.backend.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@Log4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChatService(ChatRepository chatRepository,
                       UserRepository userRepository,
                       MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
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

    public List<ChatViewDTO> getChatsFromUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        List<Chat> chats = chatRepository.findByUser(user);
        List<ChatViewDTO> dtos = new ArrayList<>();
        chats.forEach(chat -> dtos.add(ChatViewDTO.builder()
                .id(chat.getId())
                .name(chat.getName())
                .userCount(chat.getUsers().size())
                .build()));

        return dtos;
    }

    public List<MessageFromChatDTO> getMessageFromChat(UUID chatId) {
        List<Message> messages = messageRepository.findAllByChatIdOrderByCreatedAtDesc(chatId);

        List<MessageFromChatDTO> dtos = new ArrayList<>(messages.size());

        messages.forEach(message -> dtos.add(MessageFromChatDTO.builder()
                .id(message.getId())
                .text(message.getText())
                .type("text")
                .author(UserFromMessageDTO.builder()
                        .id(String.valueOf(message.getSender().getId()))
                        .firstName(message.getSender().getFirstname())
                        .lastName(message.getSender().getLastname())
                        .build())
                .createdAt(message.getCreatedAt().toEpochMilli())
                .build()));

        return dtos;
    }

    public String sendMessage(UUID chatId, MessageDTO dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message();

        message.setChat(chat);
        message.setSender(userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found")));
        message.setText(dto.getText());
        message.setCreatedAt(Instant.now());

        messageRepository.save(message);

        return "OK";
    }

    public String leaveChat(UUID chatId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Chat chat = chatRepository
                .findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Нет такого чата"));

        Iterator<User> iterator = chat.getUsers().iterator();
        while (iterator.hasNext()) {
            User u = iterator.next();
            if (u.getId().equals(user.getId())) {
                iterator.remove();
            }
        }

        if (chat.getUsers().isEmpty()) {
            chatRepository.delete(chat);
            return "Вы вышли из чата";
        }

        chatRepository.save(chat);
        return "Вы вышли из чата";
    }


}


package ru.ncti.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.backend.dto.ChatDTO;
import ru.ncti.backend.dto.MessageDTO;
import ru.ncti.backend.service.ChatService;

import java.util.UUID;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping()
    public ResponseEntity<?> getChatsFromUser() {
        return ResponseEntity.ok(chatService.getChatsFromUser());
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getMessagesFromChat(@PathVariable("chatId") UUID uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getMessageFromChat(uuid));
    }


    @PostMapping("/create")
    public ResponseEntity<?> createChatroom(@RequestBody ChatDTO chatDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.createChatroom(chatDTO));
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<?> sendMessage(@PathVariable("chatId") UUID uuid, @RequestBody MessageDTO messageDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.sendMessage(uuid, messageDTO));
    }

}

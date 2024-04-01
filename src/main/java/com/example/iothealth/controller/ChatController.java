package com.example.iothealth.controller;

import com.example.iothealth.domain.ChatInfo;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.response.LatestChat;
import com.example.iothealth.payload.response.MessageContent;
import com.example.iothealth.service.AuthService;
import com.example.iothealth.service.ChatService;
import com.example.iothealth.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AuthService authService;
    private final ChatService chatService;
    private final MessageService messageService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> createInbox(@RequestParam("email") String email) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        chatService.createInbox(currentUser.getEmail(), email);
        return ResponseEntity.ok("Chat created successfully.");
    }

    @GetMapping("/chats")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<List<ChatInfo>> getChatsByCurrentUser() {
        return ResponseEntity.ok(chatService.getChatsByUser());
    }

    @DeleteMapping("/{chatId}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> deleteChat(@PathVariable("chatId") String chatId) {
        chatService.deleteChat(UUID.fromString(chatId));
        return ResponseEntity.ok("Chat deleted successfully.");
    }

    @GetMapping("/{chatId}/messages")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<List<MessageContent>> getMessagesInSelectedChat(@PathVariable("chatId") String chatId) {
        return ResponseEntity.ok(messageService.getMessagesInChat(chatId));
    }

    @GetMapping("/{userId}/latestMessages")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<LatestChat> getMessagesInLatestChat(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(messageService.getMessagesInLatestChat(userId));
    }

    @PostMapping("/message")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> sendMessage(@RequestParam("chatId") String chatId, @RequestParam("content") String content) {
        messageService.sendMessage(chatId, content);
        return ResponseEntity.ok("Message sent successfully.");
    }

    @PutMapping("/message")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> editMessage(@RequestParam("messageId") String messageId, @RequestParam("content") String content) {
        messageService.editMessage(Integer.parseInt(messageId), content);
        return ResponseEntity.ok("Message edited successfully.");
    }

    @DeleteMapping("/message")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<?> deleteMessage(@RequestParam("messageId") Integer messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok("Message deleted successfully.");
    }
}

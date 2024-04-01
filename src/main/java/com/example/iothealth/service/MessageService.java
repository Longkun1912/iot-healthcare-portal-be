package com.example.iothealth.service;

import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.model.Chat;
import com.example.iothealth.model.Message;
import com.example.iothealth.model.Role;
import com.example.iothealth.model.User;
import com.example.iothealth.payload.response.LatestChat;
import com.example.iothealth.payload.response.MessageContent;
import com.example.iothealth.repository.ChatRepository;
import com.example.iothealth.repository.MessageRepository;
import com.example.iothealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final AuthService authService;

    public void sendMessage(String chatId, String content) {
        Chat chat;
        User user = authService.getCurrentAuthenticatedUser();
        Optional<Chat> selectedChat = chatRepository.findById(UUID.fromString(chatId));

        if (selectedChat.isEmpty()) {
            chat = chatRepository.findLatestChatByUserId(user.getId()).get(0);
        }
        else {
            chat = selectedChat.get();
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(user);
        message.setContent(content);
        message.setSent_at(LocalDateTime.now());
        chat.setUpdated_at(LocalDateTime.now());
        chatRepository.save(chat);
        messageRepository.save(message);
    }

    public void editMessage(Integer messageId, String content) {
        Message message = messageRepository.findMessageById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setContent(content);
        Chat chat = message.getChat();
        chat.setUpdated_at(LocalDateTime.now());
        chatRepository.save(chat);
        messageRepository.save(message);
    }

    public void deleteMessage(Integer messageId) {
        Message message = messageRepository.findMessageById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        Chat chat = message.getChat();
        chat.setUpdated_at(LocalDateTime.now());
        chatRepository.save(chat);
        messageRepository.delete(message);
    }

    public List<MessageContent> getMessagesInChat(String chatId) {
        Chat chat = chatRepository.findById(UUID.fromString(chatId)).orElseThrow(() -> new RuntimeException("Chat not found"));
        List<MessageContent> messageContents = new ArrayList<>();
        List<Message> messages = messageRepository.findAllByChatId(chat.getId());
        for (Message message : messages) {
            MessageContent messageContent = mapper.map(message, MessageContent.class);
            messageContent.setChatId(chat.getId());
            UserInfoDetails senderDetails = mapper.map(message.getSender(), UserInfoDetails.class);
            senderDetails.setRoles(message.getSender().getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
            senderDetails.setOrganisation(message.getSender().getOrganisation().getName());
            messageContent.setSender(senderDetails);
            messageContents.add(messageContent);
        }
        chat.setUpdated_at(LocalDateTime.now());
        chatRepository.save(chat);
        return messageContents;
    }

    public LatestChat getMessagesInLatestChat(String userId){
        LatestChat latestChatResponse = new LatestChat();
        if (!chatRepository.findLatestChatByUserId(UUID.fromString(userId)).isEmpty()) {
            Chat latestChat = chatRepository.findLatestChatByUserId(UUID.fromString(userId)).get(0);
            User secondMember = null;
            if (latestChat.getMember1().getId().toString().equals(userId)) {
                secondMember = latestChat.getMember2();
            } else if (latestChat.getMember2().getId().toString().equals(userId)) {
                secondMember = latestChat.getMember1();
            }
            else {
                throw new RuntimeException("Second member not found");
            }
            latestChatResponse.setChatId(latestChat.getId());
            latestChatResponse.setLatestMessages(getMessagesInChat(latestChat.getId().toString()));

        }
        return latestChatResponse;
    }
}

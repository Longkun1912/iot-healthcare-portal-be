package com.example.iothealth.service;

import com.example.iothealth.domain.ChatInfo;
import com.example.iothealth.domain.UserInfoDetails;
import com.example.iothealth.model.Role;
import com.example.iothealth.payload.response.MessageContent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.iothealth.model.Chat;
import com.example.iothealth.model.User;
import com.example.iothealth.repository.ChatRepository;
import com.example.iothealth.repository.MessageRepository;
import com.example.iothealth.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final AuthService authService;
    private final ModelMapper mapper;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public void createInbox(String currentEmail, String memberEmail) {
        User currentUser = userRepository.findUserByEmail(currentEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User secondMember = userRepository.findUserByEmail(memberEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if a chat already exists between the two users
        List<Chat> existingChats = chatRepository.findAllChatsByUserId(currentUser.getId());
        for (Chat chat : existingChats) {
            if ((chat.getMember1().equals(currentUser) && chat.getMember2().equals(secondMember)) ||
                (chat.getMember1().equals(secondMember) && chat.getMember2().equals(currentUser))) {
                // A chat already exists between the two users, so return without creating a new one
                return;
            }
        }

        // If no existing chat was found, create a new one
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.setMember1(currentUser);
        chat.setMember2(secondMember);

        chat.setCreated_at(LocalDateTime.now());
        chat.setUpdated_at(LocalDateTime.now());
        chatRepository.save(chat);
    }

    public void deleteChat(UUID chatId) {
        chatRepository.deleteById(chatId);
    }

    public List<ChatInfo> getChatsByUser(){
        User currentUser = authService.getCurrentAuthenticatedUser();
        List<ChatInfo> chatLogs = new ArrayList<>();
        List<Chat> chats = chatRepository.findAllChatsByUserId(currentUser.getId());

        for (Chat chat : chats){
            ChatInfo chatInfo = mapper.map(chat, ChatInfo.class);

            User secondMember = null;
            if (chat.getMember1().equals(currentUser)) {
                secondMember = chat.getMember2();
            } else if (chat.getMember2().equals(currentUser)) {
                secondMember = chat.getMember1();
            }

            if (secondMember != null){
                chatInfo.setName(secondMember.getUsername());
                if (secondMember.getAvatar() != null && !secondMember.getAvatar().isEmpty()){
                    chatInfo.setImage(secondMember.getAvatar());
                } else {
                    chatInfo.setImage("https://static.thenounproject.com/png/862013-200.png");
                }
            } else {
                throw new UsernameNotFoundException("User not found");
            }

            chatInfo.setOtherMemberId(secondMember.getId());

            MessageContent latestMessage = messageRepository.findLatestByChatId(chat.getId()).stream()
                    .map(message -> {
                        MessageContent messageContent = mapper.map(message, MessageContent.class);
                        UserInfoDetails senderDetails = mapper.map(message.getSender(), UserInfoDetails.class);
                        senderDetails.setRoles(message.getSender().getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
                        senderDetails.setOrganisation(message.getSender().getOrganisation().getName());
                        messageContent.setSender(senderDetails);
                        return messageContent;
                    })
                    .findFirst()
                    .orElse(new MessageContent("No message yet", LocalDateTime.now()));

            chatInfo.setLatestMessage(latestMessage);
            chatLogs.add(chatInfo);
        }
        return chatLogs;
    }
}

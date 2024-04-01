package com.example.iothealth.domain;

import com.example.iothealth.payload.response.MessageContent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChatInfo {
    private UUID id;

    private String image;

    private String name;

    private UUID otherMemberId;

    private MessageContent latestMessage;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
}

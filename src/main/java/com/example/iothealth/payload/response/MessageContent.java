package com.example.iothealth.payload.response;

import com.example.iothealth.domain.UserInfoDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageContent {
    @JsonProperty
    private int id;

    @JsonProperty
    UUID chatId;
    @JsonProperty
    private String content;

    @JsonProperty
    private UserInfoDetails sender;

    @JsonProperty
    private LocalDateTime sent_at;

    public MessageContent() {
    }

    public MessageContent(String content, LocalDateTime sent_at) {
        this.content = content;
        this.sent_at = sent_at;
    }
}

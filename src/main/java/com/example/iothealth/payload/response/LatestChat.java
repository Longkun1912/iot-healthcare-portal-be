package com.example.iothealth.payload.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LatestChat {
    private UUID chatId;

    private List<MessageContent> latestMessages;
}

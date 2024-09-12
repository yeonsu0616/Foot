package com.example.foot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private String content;
    private String sender;

    public ChatMessage(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }
}
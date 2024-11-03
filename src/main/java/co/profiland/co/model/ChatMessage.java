package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 3L;

    private UUID id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;

    // Constructors, getters, and setters
    public ChatMessage(String senderId, String receiverId, String content) {
        this.id = UUID.randomUUID();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}
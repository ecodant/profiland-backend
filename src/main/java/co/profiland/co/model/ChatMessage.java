package co.profiland.co.model;

import lombok.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ChatMessage implements Serializable {
    private String senderName;
    private String receiverName;
    private String message;
    private String timestamp;
    private Status status;
    public enum Status {
        JOIN,
        MESSAGE,
        LEAVE
    }
}
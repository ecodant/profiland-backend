package co.profiland.co.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatHistoryRequest {
    private String currentUserId;
    private String otherUserId;
}


package co.profiland.co.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Data;

import java.io.Serializable;

@Data
public class Chat implements Serializable {
   private static final long serialVersionUID = 4L;

    private String chatRoomId;
    private String user1Id;
    private String user2Id;
    private List<ChatMessage> messages;

    public Chat(String user1Id, String user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.chatRoomId = generateChatRoomId(user1Id, user2Id);
        this.messages = new ArrayList<>();
    }

    private String generateChatRoomId(String user1Id, String user2Id) {
        List<String> sortedIds = Arrays.asList(user1Id, user2Id);
        Collections.sort(sortedIds);
        return String.join("_", sortedIds);
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }
}

package co.profiland.co.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.model.ChatMessage;
import co.profiland.co.utils.Utilities;

@Service
public class ChatMessageService {

    private static final String XML_PATH = "C:/td/persistence/models/chatmessages/chatmessage.dat";
    private final Utilities persistence;
    private final ThreadPoolManager threadPool;

    public ChatMessageService(Utilities persistence) throws PersistenceException, BackupException {
        this.persistence = persistence;
        this.threadPool = ThreadPoolManager.getInstance();
        persistence.initializeFile(XML_PATH, new ArrayList<ChatMessage>());
    }

    // Create a new ChatMessage
    public CompletableFuture<ChatMessage> createChatMessage(ChatMessage chatMessage) {
        return threadPool.submitTask(() -> {
            List<ChatMessage> messages = getAllChatMessages().get();
            messages.add(chatMessage);

            persistence.serializeObject(XML_PATH, messages);
            persistence.writeIntoLogger("ChatMessage from " + chatMessage.getSenderName() + " created", Level.FINE);
            return chatMessage;
        });
    }

    // Retrieve all ChatMessages
    @SuppressWarnings("unchecked")
    public CompletableFuture<List<ChatMessage>> getAllChatMessages() {
        return threadPool.submitTask(() -> {
            Object deserializedData = persistence.deserializeObject(XML_PATH);
            if (deserializedData instanceof List<?>) {
                persistence.writeIntoLogger("Retrieved all chat messages successfully", Level.FINE);
                return (List<ChatMessage>) deserializedData;
            }
            return new ArrayList<>();
        });
    }

    // Update an existing ChatMessage
    public CompletableFuture<ChatMessage> updateChatMessage(int index, ChatMessage updatedMessage) {
        return threadPool.submitTask(() -> {
            List<ChatMessage> messages = getAllChatMessages().get();

            if (index >= 0 && index < messages.size()) {
                messages.set(index, updatedMessage);
                persistence.serializeObject(XML_PATH, messages);
                persistence.writeIntoLogger("ChatMessage at index " + index + " was updated", Level.FINE);
                return updatedMessage;
            } else {
                persistence.writeIntoLogger("ChatMessage at index " + index + " not found for update", Level.WARNING);
                return null;
            }
        });
    }

    // Delete a ChatMessage
    public CompletableFuture<Boolean> deleteChatMessage(int index) {
        return threadPool.submitTask(() -> {
            List<ChatMessage> messages = getAllChatMessages().get();
            if (index >= 0 && index < messages.size()) {
                messages.remove(index);
                persistence.serializeObject(XML_PATH, messages);
                persistence.writeIntoLogger("ChatMessage at index " + index + " was deleted", Level.FINE);
                return true;
            } else {
                persistence.writeIntoLogger("ChatMessage at index " + index + " not found for deletion", Level.WARNING);
                return false;
            }
        });
    }
}

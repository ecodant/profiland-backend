package co.profiland.co.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.model.Message;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageService {
    
    private static final String XML_PATH = "src/main/resources/messages/messages.xml";
    private final Utilities persistence = Utilities.getInstance();

    public MessageService() {
        persistence.initializeFile(XML_PATH, new ArrayList<Message>());
    }

    public Message saveMessage(Message message) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessages();

        if (message.getId() == null || message.getId().isEmpty()) {
            message.setId(UUID.randomUUID().toString());
        }
        messages.add(message);
        persistence.serializeObject(XML_PATH, messages);

        log.info("Saved Review with ID: {}", message.getId());
    
        return message;
    }

    @SuppressWarnings("unchecked")
    public List<Message> getAllMessages() throws IOException, ClassNotFoundException {
        Object deserializedData = persistence.deserializeObject(XML_PATH);
        if (deserializedData instanceof List<?>) {
            return (List<Message>) deserializedData;
        }
        return new ArrayList<>();
    }
    public Message updateMessage(String id, Message updatedMessage) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessages();

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(id)) {
                updatedMessage.setId(id);
                messages.set(i, updatedMessage);
                persistence.serializeObject(XML_PATH, messages);
                log.info("Message Updated ID: {}", id);
                return updatedMessage;
            }
        }
        log.warn("Review not found for bro. ID: {}", id);
        return null; 
    }

    public boolean deleteMessage(String id) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessages();
        boolean removed = messages.removeIf(Message -> Message.getId().equals(id));
        if (removed) {
            persistence.serializeObject(XML_PATH, messages);
            log.info("Deleted message with ID: {}", id);
        } else {
            log.warn("The Message that you wanna deleted was not found. ID: {}", id);
        }
        return removed;
    }
  
    public Message findMessageById(String id) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessages();
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String convertToJson(List<Message> messages) throws IOException {
        return persistence.convertToJson(messages);
    }

}

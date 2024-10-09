package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.Message;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageService {
    
    private static final String XML_PATH = "src/main/resources/messages/messages.xml";
    private static final String DAT_PATH = "src/main/resources/messages/messages.dat";

    private final Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Message saveMessage(Message message, String format) throws IOException, ClassNotFoundException {
        List<Message> messages;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            messages = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, messages);
            } else {
                persistence.serializeObject(DAT_PATH, messages);
            }
        } else {
            // If the file exists, read the existing sellers
            messages = getAllMessages(format);
        }

        messages.add(message);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, messages);
        } else {
            persistence.serializeObject(DAT_PATH, messages);
        }

        return message;
    }

    // Retrieve all sellers by merging XML and DAT files 
    public List<Message> getAllMessagesMerged() throws IOException, ClassNotFoundException {
        List<Message> xmlMessages = getAllMessages("xml");
        List<Message> datMessages = getAllMessages("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Message> mergedMessages = new ArrayList<>();

        for (Message message : xmlMessages) {
            if (seenIds.add(message.getId())) {
                mergedMessages.add(message);
            }
        }

        for (Message message : datMessages) {
            if (seenIds.add(message.getId())) {
                mergedMessages.add(message);
            }
        }

        return mergedMessages;
    }
    public Message updateMessage(String id, Message updatedMessage, String format) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessages(format);

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedMessage.setId(id);
                messages.set(i, updatedMessage);  // Replace existing Message with updated data
                serializeMessages(format, messages);
                return updatedMessage;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteMessage(String id) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessagesMerged();
        boolean removed = messages.removeIf(Message -> Message.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeMessages(id, messages);
            serializeMessages("xml", messages);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Message> getAllMessages(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Message>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeMessages(String format, List<Message> messages) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, messages);
        } else {
            persistence.serializeObject(DAT_PATH, messages);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Message> messages) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(messages);
    }

    public Message findMessageById(String id) throws IOException, ClassNotFoundException {
        List<Message> messages = getAllMessagesMerged();
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

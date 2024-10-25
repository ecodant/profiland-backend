package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import co.profiland.co.model.Chat;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {

    private static final String XML_PATH = "src/main/resources/chats/chats.xml";
    private static final String DAT_PATH = "src/main/resources/chats/chats.dat";

    private final Utilities persistence = Utilities.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Chat saveChat(Chat chat, String format) throws IOException, ClassNotFoundException {
        List<Chat> chats;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            chats = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObject(XML_PATH, chats);
            } else {
                persistence.serializeObject(DAT_PATH, chats);
            }
        } else {
            // If the file exists, read the existing Chats
            chats = getAllChats(format);
        }

        chats.add(chat);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObject(XML_PATH, chats);
        } else {
            persistence.serializeObject(DAT_PATH, chats);
        }

        return chat;
    }

    //FOR THE FUTURE, BUT WE CAN UPDATED THE WHOLE OBJECT INSTEAD
    //  public Chat addContact(String ChatId, String contactId) throws IOException, ClassNotFoundException {
    //     Chat Chat = findChatById(ChatId);
    //     if (Chat != null && Chat.addContact(contactId)) {
    //         saveChat(Chat, "dat"); 
    //     }
    //     return Chat;
    // }

    // public Chat removeContact(String ChatId, String contactId) throws IOException, ClassNotFoundException {
    //     Chat Chat = findChatById(ChatId);
    //     if (Chat != null && Chat.removeContact(contactId)) {
    //         saveChat(Chat, "dat"); 
    //     }
    //     return Chat;
    // }


    // Retrieve all Chats by merging XML and DAT files 
    public List<Chat> getAllChatsMerged() throws IOException, ClassNotFoundException {
        List<Chat> xmlChats = getAllChats("xml");
        List<Chat> datChats = getAllChats("dat");

        // This Set eliminates duplicates based on the Chat ID
        Set<String> seenIds = new HashSet<>();
        List<Chat> mergedChats = new ArrayList<>();

        for (Chat chat : xmlChats) {
            if (seenIds.add(chat.getId())) {
                mergedChats.add(chat);
            }
        }

        for (Chat chat : datChats) {
            if (seenIds.add(chat.getId())) {
                mergedChats.add(chat);
            }
        }

        return mergedChats;
    }
    public Chat updateChat(String id, Chat updatedChat, String format) throws IOException, ClassNotFoundException {
        List<Chat> chats = getAllChats(format);

        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedChat.setId(id);
                chats.set(i, updatedChat);  // Replace existing Chat with updated data
                serializeChats(format, chats);
                return updatedChat;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a Chat by ID
    public boolean deleteChat(String id) throws IOException, ClassNotFoundException {
        List<Chat> chats = getAllChatsMerged();
        boolean removed = chats.removeIf(Chat -> Chat.getId().equals(id));
        //This was cause an error because if a Chat it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeChats("dat", chats);
            serializeChats("xml", chats);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Chat> getAllChats(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObject(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Chat>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    private void serializeChats(String format, List<Chat> chats) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObject(XML_PATH, chats);
        } else {
            persistence.serializeObject(DAT_PATH, chats);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Chat> chats) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(chats);
    }

    public Chat findChatById(String id) throws IOException, ClassNotFoundException {
        List<Chat> chats = getAllChatsMerged();
        return chats.stream()
                .filter(chat -> chat.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

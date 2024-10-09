package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Chat;
import co.profiland.co.service.ChatService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/Chats") 
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Create: Save a new Chat
    @PostMapping("/save")
    public ResponseEntity<Chat> saveChat(@RequestBody Chat chat,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Chat savedChat = chatService.saveChat(chat, format);
            return ResponseEntity.ok(savedChat);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Chats
    @GetMapping("/")
    public ResponseEntity<String> getAllChats() {
        try {
            List<Chat> chats = chatService.getAllChatsMerged();
            return ResponseEntity.ok(chatService.convertToJson(chats));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Chat> getChatById(@RequestParam("id") String id) {
        try {
            Chat chat = chatService.findChatById(id);
            return chat != null ? ResponseEntity.ok(chat) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chat> updateChat(@PathVariable String id,
                                               @RequestBody Chat chat,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Chat updatedChat = chatService.updateChat(id, chat, format);
            return updatedChat != null ? ResponseEntity.ok(updatedChat) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Chat by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable String id) {
        try {
            boolean isDeleted = chatService.deleteChat(id);
            return isDeleted ? ResponseEntity.ok("Chat deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

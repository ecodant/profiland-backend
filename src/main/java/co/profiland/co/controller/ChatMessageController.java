package co.profiland.co.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.ChatMessage;
import co.profiland.co.service.ChatMessageService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/profiland/chatmessages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/")
    public ResponseEntity<ChatMessage> createChatMessage(@RequestBody ChatMessage chatMessage) {
        try {
            ChatMessage createdMessage = chatMessageService.createChatMessage(chatMessage).get();
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<ChatMessage>> getAllChatMessages() {
        try {
            List<ChatMessage> messages = chatMessageService.getAllChatMessages().get();
            return ResponseEntity.ok(messages);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{index}")
    public ResponseEntity<ChatMessage> updateChatMessage(@PathVariable int index, @RequestBody ChatMessage chatMessage) {
        try {
            ChatMessage updatedMessage = chatMessageService.updateChatMessage(index, chatMessage).get();
            return updatedMessage != null ? 
                ResponseEntity.ok(updatedMessage) : 
                ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{index}")
    public ResponseEntity<Void> deleteChatMessage(@PathVariable int index) {
        try {
            boolean deleted = chatMessageService.deleteChatMessage(index).get();
            return deleted ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
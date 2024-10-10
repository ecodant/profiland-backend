package co.profiland.co.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.profiland.co.model.Message;
import co.profiland.co.service.MessageService;

@RestController
@RequestMapping("/profiland/messages") 
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/")
    public ResponseEntity<Message> saveMessage(@RequestBody Message message) {
        try {
            Message savedMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(savedMessage);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> getAllMessages() {
        try {
            List<Message> messages = messageService.getAllMessages();
            return ResponseEntity.ok(messageService.convertToJson(messages));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable String id) {
        try {
            Message message = messageService.findMessageById(id);
            return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable String id, @RequestBody Message message) {
        try {
            Message updatedMessage = messageService.updateMessage(id, message);
            return updatedMessage != null ? ResponseEntity.ok(updatedMessage) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable String id) {
        try {
            boolean isDeleted = messageService.deleteMessage(id);
            return isDeleted ? ResponseEntity.ok("Message deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

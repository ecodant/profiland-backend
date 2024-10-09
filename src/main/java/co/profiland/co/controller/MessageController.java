package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Message;
import co.profiland.co.service.MessageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/Messages") 
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Create: Save a new Message
    @PostMapping("/save")
    public ResponseEntity<Message> saveMessage(@RequestBody Message message,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Message savedMessage = messageService.saveMessage(message, format);
            return ResponseEntity.ok(savedMessage);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Messages
    @GetMapping("/")
    public ResponseEntity<String> getAllMessages() {
        try {
            List<Message> messages = messageService.getAllMessagesMerged();
            return ResponseEntity.ok(messageService.convertToJson(messages));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Message> getMessageById(@RequestParam("id") String id) {
        try {
            Message message = messageService.findMessageById(id);
            return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable String id,
                                               @RequestBody Message message,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Message updatedMessage = messageService.updateMessage(id, message, format);
            return updatedMessage != null ? ResponseEntity.ok(updatedMessage) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Message by ID
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

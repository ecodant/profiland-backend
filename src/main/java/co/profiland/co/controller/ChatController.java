
package co.profiland.co.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import co.profiland.co.model.ChatMessage;
import co.profiland.co.service.ChatMessageService;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatMessageService service;

    public ChatController(ChatMessageService service) {
        this.service = service;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public ChatMessage receiveMessage(@Payload ChatMessage message){
        return message;
    }

    @MessageMapping("/private-message")
    public ChatMessage recMessage(@Payload ChatMessage message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        service.createChatMessage(message);
        System.out.println(message.toString());
        return message;
    }
}
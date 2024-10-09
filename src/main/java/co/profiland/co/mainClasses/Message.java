package co.profiland.co.mainClasses;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Message {
    private Seller targetSeller;
    private String content;
    private LocalDateTime dateSending;

    public Message(){

    }

    public Message(Seller targetSeller, String content, LocalDateTime dateSending){
        this.targetSeller = targetSeller;
        this.content = content;
        this.dateSending = dateSending;
    }

}

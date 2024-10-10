package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Message implements Serializable {
    private String id;
    private String targetSellerId;
    private String reciverSellerId;
    private String content;
    private LocalDateTime dateSending;

    public Message(){

    }

    public Message(String targetSellerId, String reciverSellerId, String content, LocalDateTime dateSending){
        this.targetSellerId = targetSellerId;
        this.reciverSellerId = reciverSellerId;
        this.content = content;
        this.dateSending = dateSending;
    }

}

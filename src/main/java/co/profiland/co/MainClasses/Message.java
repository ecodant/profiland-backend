package mainClasses;

import java.time.LocalDateTime;

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

    public Seller getTargetSeller() {
        return targetSeller;
    }

    public void setTargetSeller(Seller targetSeller) {
        this.targetSeller = targetSeller;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateSending() {
        return dateSending;
    }

    public void setDateSending(LocalDateTime dateSending) {
        this.dateSending = dateSending;
    }
}

package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Comment implements Serializable{
    private String id;
    private String productRef;
    private String content;
    private LocalDateTime date;

    public Comment(){

    }

    public Comment(String id, String productRef, String content, LocalDateTime date){
        this.id = id;
        this.productRef = productRef;
        this.content = content;
        this.date= date;
    }

}

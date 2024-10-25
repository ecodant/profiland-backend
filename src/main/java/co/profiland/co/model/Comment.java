package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Comment implements Serializable{
    private String id;
    private String author;
    private String content;
    private LocalDateTime date;

    public Comment(){

    }

    public Comment(String id, String author, String content, LocalDateTime date){
        this.id = id;
        this.author = author;
        this.content = content;
        this.date= date;
    }

}

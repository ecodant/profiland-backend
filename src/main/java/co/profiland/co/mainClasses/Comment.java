package co.profiland.co.mainClasses;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Comment {
    private Seller author;
    private String text;
    private LocalDateTime dateComment;

    public Comment(){

    }

    public Comment(Seller author, String text, LocalDateTime dateComment){
        this.author = author;
        this.text = text;
        this.dateComment = dateComment;
    }
}

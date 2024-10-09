package co.profiland.co.model;

import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;

@Data
public class Comment implements Serializable{
    private String id;
    private Seller author;
    private String text;
    private LocalDateTime dateComment;

    public Comment(){

    }

    public Comment(String id, Seller author, String text, LocalDateTime dateComment){

        this.id = id;
        this.author = author;
        this.text = text;
        this.dateComment = dateComment;
    }

}

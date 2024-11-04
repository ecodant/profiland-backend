package co.profiland.co.model;

import java.io.Serializable;

import lombok.Data;
@Data
public class Review implements Serializable{
    private String authorId;
    private String authorName;
    private String comment;
    private Integer calification;

    public Review(){

    }

    public Review(String author,String authorName, String comment, Integer calification){
        this.authorId = author;
        this.authorName = authorName;
        this.comment = comment;
        this.calification = calification;
    }

}

package co.profiland.co.model;

import java.io.Serializable;

import lombok.Data;
@Data
public class Review implements Serializable{
    private String authorRef;
    private String comment;
    private Integer calification;

    public Review(){

    }

    public Review(String author, String comment, Integer calification){
        this.authorRef = author;
        this.comment = comment;
        this.calification = calification;
    }

}

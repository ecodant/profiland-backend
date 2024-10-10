package co.profiland.co.model;

import java.io.Serializable;

import lombok.Data;
@Data
public class Review implements Serializable{
    private String id;
    private String authorRef;
    private String ownerRef;
    private String comment;
    private Integer calification;

    public Review(){

    }

    public Review(String author, String ownerRef, String comment, Integer calification){
        this.authorRef = author;
        this.ownerRef = ownerRef;
        this.comment = comment;
        this.calification = calification;
    }

}

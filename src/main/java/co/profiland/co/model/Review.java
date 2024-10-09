package co.profiland.co.model;

import java.io.Serializable;

import lombok.Data;
@Data
public class Review implements Serializable{
    private String id;
    private Seller author;
    private String comment;
    private Integer calification;

    public Review(){

    }

    public Review(Seller author, String comment, Integer calification){
        this.author = author;
        this.comment = comment;
        this.calification = calification;
    }

}

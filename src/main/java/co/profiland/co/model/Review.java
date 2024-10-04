package co.profiland.co.model;

import java.io.Serializable;
public class Review implements Serializable{
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

    public Seller getAuthor() {
        return author;
    }

    public void setAuthor(Seller author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCalification() {
        return calification;
    }

    public void setCalification(Integer calification) {
        this.calification = calification;
    }
}

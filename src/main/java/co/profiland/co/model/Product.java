package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 2L;

    private String id;
    private String name;
    private String code;
    private String image;
    private String category;
    private Integer price;
    private String state;
    private LocalDateTime publicationDate;
    private ArrayList<Comment> comments;
    private Integer likes;
    private String sellerId;

    public Product(){
    }

    public Product(String name, String code, String image, String category, Integer price, String state, LocalDateTime publicationDate, ArrayList<Comment> comments, Integer likes, String sellerId){
        this.name = name;
        this.code = code;
        this.image = image;
        this.category = category;
        this.price = price;
        this.state = state;
        this.publicationDate = publicationDate;
        this.comments = comments;
        this.likes = likes;
        this.sellerId = sellerId;
    }

    public Product(String name, String code, String image, String category, Integer price, String state, LocalDateTime publicationDate, Integer likes, String sellerId){
        this.name = name;
        this.code = code;
        this.image = image;
        this.category = category;
        this.price = price;
        this.state = state;
        this.publicationDate = publicationDate;
        this.comments = new ArrayList<Comment>();
        this.likes = likes;
        this.sellerId = sellerId;
    }

    public void addComment(){

    }

    public Integer registerLike(){
        return 0;
    }

}

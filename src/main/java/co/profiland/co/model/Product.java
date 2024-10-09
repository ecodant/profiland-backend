package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import lombok.Data;

@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String code;
    private String image;
    private String category;
    private Integer prize;
    private State state;
    private LocalDateTime publicationDate;
    private ArrayList<Comment> comments;
    private Integer like;
    private Seller seller;

    public Product(){
    }

    public Product(String name, String code, String image, String category, Integer prize, State state, LocalDateTime publicationDate, ArrayList<Comment> comments, Integer like, Seller seller){
        this.name = name;
        this.code = code;
        this.image = image;
        this.category = category;
        this.prize = prize;
        this.state = state;
        this.publicationDate = publicationDate;
        this.comments = comments;
        this.like = like;
        this.seller = seller;
    }

    public Product(String name, String code, String image, String category, Integer prize, State state, LocalDateTime publicationDate, Integer like, Seller seller){
        this.name = name;
        this.code = code;
        this.image = image;
        this.category = category;
        this.prize = prize;
        this.state = state;
        this.publicationDate = publicationDate;
        this.comments = new ArrayList<Comment>();
        this.like = like;
        this.seller = seller;
    }

    public void addComment(){

    }

    public Integer registerLike(){
        return 0;
    }

}

package co.profiland.co.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Product {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPrize() {
        return prize;
    }

    public void setPrize(Integer prize) {
        this.prize = prize;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}

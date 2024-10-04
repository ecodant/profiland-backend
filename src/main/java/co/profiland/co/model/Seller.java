package co.profiland.co.model;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.Data;


@Data
public class Seller implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String lastName;
    private String license;
    private String address;
    private ArrayList<Seller> contacts;
    private ArrayList<Product> products;
    private StatsPanel stats;
    private ArrayList<Chat> chats;
    private Wall wall;

    public Seller(){
    }

    public Seller(String name, String lastName, String license, String address, ArrayList<Seller> contacts, ArrayList<Product> products, StatsPanel stats, ArrayList<Chat> chats, Wall wall){
        this.name = name;
        this.lastName = lastName;
        this.license = license;
        this.address = address;
        this.contacts = contacts;
        this.products = products;
        this.stats = stats;
        this.chats = chats;
        this.wall = wall;
    }

    public Seller(String name, String lastName, String license, String address, StatsPanel stats, Wall wall){
        this.name = name;
        this.lastName = lastName;
        this.license = license;
        this.address = address;
        this.contacts = new ArrayList<Seller>();
        this.products = new ArrayList<Product>();
        this.stats = stats;
        this.chats = new ArrayList<Chat>();
        this.wall = wall;
    }

    public void addContact(){

    }

    public void sendMessage(){

    }

    public void publishProduct(Product product){

    }

    public void commentProduct(Product product, Comment comment){

    }

    public void giveLike(Product product){

    }

    public void requestLink(Seller seller){

    }

}

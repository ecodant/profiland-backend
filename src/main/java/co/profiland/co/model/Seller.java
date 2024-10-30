package co.profiland.co.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class Seller implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String email;
    private String password;
    private String lastName;
    private String license;
    private String address;
    private String profileImg;
    
    // Reference fields to other entities (Avoiding nested objects)
    private ArrayList<Review> reviews;
    // private Set<String> reviews;
    private Set<String> contacts;
    private Set<String> products;
    private Set<String> stats;
    private Set<String> chats;
    private Set<String> contactRequests;
    private Wall wall;

    public Seller() {
        this.contacts = new HashSet<>();
        this.products = new HashSet<>();
        this.stats = new HashSet<>();
        this.chats = new HashSet<>();
    }

    public Seller(String name, String lastName,String email,String password, String license, String address, String profileImg, ArrayList<Review> reviews, Set<String> contacts, Set<String> products, Set<String> stats, Set<String> chats, Set<String> contactRequests, Wall wall) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.license = license;
        this.address = address;
        this.profileImg = profileImg != null ? profileImg: "";
        this.reviews = reviews != null ? reviews : new ArrayList<Review>();
        this.contacts = contacts != null ? contacts : new HashSet<>();
        this.products = products != null ? products : new HashSet<>();
        this.stats = stats;
        this.chats = chats != null ? chats : new HashSet<>();
        this.contactRequests = contactRequests;
        this.wall = wall;
    }

    public Seller(String name, String email, String password, String lastName, String license, String address, String profileImg, List<Stadistic> stats, Wall wall) {
        this(name, email, password, lastName, license, address, profileImg, new ArrayList<Review>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), wall);
    }

    public boolean addContact(String contactId) {
        if (contactId != null && !contactId.equals(this.id)) {
            return contacts.add(contactId);
        }
        return false;
    }
    // The next two methods its more for the Front end part 
    public boolean removeContact(String contactId) {
        return contacts.remove(contactId);
    }

    // Check if a seller is already a contact
    public boolean isContact(String contactId) {
        return contacts.contains(contactId);
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

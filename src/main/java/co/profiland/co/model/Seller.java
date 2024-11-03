package co.profiland.co.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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

    private ArrayList<Review> reviews;
    private Set<String> contacts;
    private Set<SellerNotification> notifications;
    private ArrayList<Product> products;
    private ArrayList<Chat> chats;
    private ArrayList<ContactRequest> contactRequests;

    // Default constructor required for Jackson
    public Seller() {}

    // Existing constructors
    public Seller(String name, String lastName, String email, String password, String license, String address, 
                  String profileImg, ArrayList<Review> reviews, Set<String> contacts,Set<SellerNotification> notifications, 
                  ArrayList<Product> products, ArrayList<Chat> chats, ArrayList<ContactRequest> contactRequests) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.license = license;
        this.address = address;
        this.profileImg = profileImg != null ? profileImg : "";
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        this.contacts = contacts != null ? contacts : new HashSet<>();
        this.notifications = notifications != null ? notifications : new HashSet<>();
        this.products = products != null ? products : new ArrayList<>();
        this.chats = chats != null ? chats : new ArrayList<>();
        this.contactRequests = contactRequests != null ? contactRequests : new ArrayList<>();
    }

    public Seller(String name, String email, String password, String lastName, String license, 
                  String address, String profileImg) {
        this(name, lastName, email, password, license, address, profileImg, 
             new ArrayList<>(), new HashSet<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public boolean addContact(String contactId) {
        if (contactId != null && !contactId.equals(this.id)) {
            return contacts.add(contactId);
        }
        return false;
    }

    public boolean removeContact(String contactId) {
        return contacts.remove(contactId);
    }

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

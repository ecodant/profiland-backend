package co.profiland.co.model;

import java.io.Serializable;
import java.util.*;


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
    //private ArrayList<Chat> chats;
    private ArrayList<ContactRequest> contactRequests;

    public Seller() {}

    // Existing constructors
    public Seller(String name, String lastName, String email, String password, String license, String address, 
                  String profileImg, ArrayList<Review> reviews, Set<String> contacts,Set<SellerNotification> notifications, 
                  ArrayList<Product> products, ArrayList<ContactRequest> contactRequests) {
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
        // this.chats = chats != null ? chats : new ArrayList<>();
        this.contactRequests = contactRequests != null ? contactRequests : new ArrayList<>();
    }

    public Seller(String name, String email, String password, String lastName, String license, 
                  String address, String profileImg) {
        this(name, lastName, email, password, license, address, profileImg, 
             new ArrayList<>(), new HashSet<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>());
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


    public double calculateAverageSoldProducts(){
        int soldProducts = 0;
        double prizeAccumulador = 0;
        for(Product p : products){
            if (p.getState().equals(State.SOLD)) {
                soldProducts++;
                prizeAccumulador = prizeAccumulador + p.getPrice();
            }
        }
        return prizeAccumulador / soldProducts;
    }

    public int getSoldProducts(){
        int soldProducts = 0;
        for (Product p : products) {
            if (p.getState().equals(State.SOLD)) {
                soldProducts++;
            }
        }
        return soldProducts;
    }

    public List<Map.Entry<String,Integer>> getBestClients(){
        ArrayList<String> clients = new ArrayList<>();
        Map<String, Integer> occurrences = new HashMap<>();
        for (Review r : reviews) {
            clients.add(r.getAuthorName());
        }
        
        for(String c : clients){
            occurrences.put(c, occurrences.getOrDefault(c, 0) + 1);
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(occurrences.entrySet()); 
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        return list;
    }

}

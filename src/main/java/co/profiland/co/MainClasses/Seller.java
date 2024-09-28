package mainClasses;

import java.util.ArrayList;

public class Seller {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Seller> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Seller> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public StatsPanel getStats() {
        return stats;
    }

    public void setStats(StatsPanel stats) {
        this.stats = stats;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }
}

package co.profiland.co.model;

import java.util.ArrayList;
import java.io.Serializable;

public class Wall implements Serializable {
    private ArrayList<Product> posts;

    public Wall(){
        this.posts = new ArrayList<Product>();
    }

    public Wall(ArrayList<Product> posts){
        this.posts = posts;
    }

    public void showWall(){}

    public void addProduct(){}

    public void showContacts(){}

    public ArrayList<Product> getPublications() {
        return posts;
    }

    public void setPublications(ArrayList<Product> posts) {
        this.posts = posts;
    }
}

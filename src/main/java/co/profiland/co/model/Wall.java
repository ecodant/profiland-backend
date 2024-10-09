package co.profiland.co.model;

import java.util.ArrayList;

import lombok.Data;

import java.io.Serializable;

@Data
public class Wall implements Serializable {
    private String id;
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
}

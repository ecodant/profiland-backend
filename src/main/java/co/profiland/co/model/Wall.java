package co.profiland.co.model;

import java.util.ArrayList;

import lombok.Data;

import java.io.Serializable;

@Data
public class Wall implements Serializable {
    private String id;
    private String idOwnerSeller;
    private ArrayList<String> postsReferences;
    
    public Wall(){
        this.postsReferences = new ArrayList<String>();
    }

    public Wall(ArrayList<String> posts,String idSeller){
        this.idOwnerSeller= idSeller;
        this.postsReferences = posts;
    }

    public void showWall(){}

    public void addProduct(){}

    public void showContacts(){}
}

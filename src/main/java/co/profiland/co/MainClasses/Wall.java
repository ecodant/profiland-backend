package mainClasses;

import java.util.ArrayList;

public class Wall {
    private ArrayList<Product> publications;

    public Wall(){
        this.publications = new ArrayList<Product>();
    }

    public Wall(ArrayList<Product> publications){
        this.publications = publications;
    }

    public void showWall(){}

    public void addProduct(){}

    public void showContacts(){}

    public ArrayList<Product> getPublications() {
        return publications;
    }

    public void setPublications(ArrayList<Product> publications) {
        this.publications = publications;
    }
}

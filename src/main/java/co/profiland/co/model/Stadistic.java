package co.profiland.co.model;

import java.io.Serializable;

public class Stadistic implements Serializable {
    private String type;
    private Integer value;

    public Stadistic(){

    }

    public Stadistic(String type, Integer value){
        this.type = type;
        this.value = value;
    }

    public void calculateStadistic(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
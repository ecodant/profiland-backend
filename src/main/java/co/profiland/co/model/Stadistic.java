package co.profiland.co.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Stadistic implements Serializable {
    private String id;
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

    public void generateStatsReport(){

    }

    public void showTopProducts(){

    }

    public void FilterForDate(LocalDateTime beginning, LocalDateTime end){

    }

}

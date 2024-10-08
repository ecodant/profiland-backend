package co.profiland.co.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatsPanel implements Serializable {
    private String id;
    private ArrayList<Stadistic> stats;

    public StatsPanel(){
        this.stats = new ArrayList<Stadistic>();
    }

    public StatsPanel(ArrayList<Stadistic> stats){
        this.stats = stats;
    }

    public void generateStatsReport(){

    }

    public void showTopProducts(){

    }

    public void FilterForDate(LocalDateTime beginning, LocalDateTime end){

    }

}

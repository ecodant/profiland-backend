package co.profiland.co.mainClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;

import lombok.Data;

@Data
public class StatsPanel {
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

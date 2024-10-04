package co.profiland.co.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public ArrayList<Stadistic> getStats() {
        return stats;
    }

    public void setStats(ArrayList<Stadistic> stats) {
        this.stats = stats;
    }
}

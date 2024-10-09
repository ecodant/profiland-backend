package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.StatsPanel;
import co.profiland.co.service.StatsPanelService;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/StatsPanels") 
public class StatsPanelController {
    private final StatsPanelService statsPanelService;

    public StatsPanelController(StatsPanelService statsPanelService) {
        this.statsPanelService = statsPanelService;
    }

    // Create: Save a new StatsPanel
    @PostMapping("/save")
    public ResponseEntity<StatsPanel> saveStatsPanel(@RequestBody StatsPanel statsPanel,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            StatsPanel savedStatsPanel = statsPanelService.saveStatsPanel(statsPanel, format);
            return ResponseEntity.ok(savedStatsPanel);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all StatsPanels
    @GetMapping("/")
    public ResponseEntity<String> getAllStatsPanels() {
        try {
            List<StatsPanel> statsPanels = statsPanelService.getAllStatsPanelsMerged();
            return ResponseEntity.ok(statsPanelService.convertToJson(statsPanels));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<StatsPanel> getStatsPanelById(@RequestParam("id") String id) {
        try {
            StatsPanel StatsPanel = statsPanelService.findStatsPanelById(id);
            return StatsPanel != null ? ResponseEntity.ok(StatsPanel) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatsPanel> updateStatsPanel(@PathVariable String id,
                                               @RequestBody StatsPanel statsPanel,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            StatsPanel updatedStatsPanel = statsPanelService.updateStatsPanel(id, statsPanel, format);
            return updatedStatsPanel != null ? ResponseEntity.ok(updatedStatsPanel) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a StatsPanel by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStatsPanel(@PathVariable String id) {
        try {
            boolean isDeleted = statsPanelService.deleteStatsPanel(id);
            return isDeleted ? ResponseEntity.ok("StatsPanel deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

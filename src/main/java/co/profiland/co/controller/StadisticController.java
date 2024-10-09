package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Stadistic;
import co.profiland.co.service.StadisticService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/Stadistics") 
public class StadisticController {
    private final StadisticService stadisticService;

    public StadisticController(StadisticService stadisticService) {
        this.stadisticService = stadisticService;
    }

    // Create: Save a new Stadistic
    @PostMapping("/save")
    public ResponseEntity<Stadistic> saveStadistic(@RequestBody Stadistic stadistic,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Stadistic savedStadistic = stadisticService.saveStadistic(stadistic, format);
            return ResponseEntity.ok(savedStadistic);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Stadistics
    @GetMapping("/")
    public ResponseEntity<String> getAllStadistics() {
        try {
            List<Stadistic> stadistics = stadisticService.getAllStadisticsMerged();
            return ResponseEntity.ok(stadisticService.convertToJson(stadistics));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Stadistic> getStadisticById(@RequestParam("id") String id) {
        try {
            Stadistic Stadistic = stadisticService.findStadisticById(id);
            return Stadistic != null ? ResponseEntity.ok(Stadistic) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stadistic> updateStadistic(@PathVariable String id,
                                               @RequestBody Stadistic stadistic,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Stadistic updatedStadistic = stadisticService.updateStadistic(id, stadistic, format);
            return updatedStadistic != null ? ResponseEntity.ok(updatedStadistic) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Stadistic by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStadistic(@PathVariable String id) {
        try {
            boolean isDeleted = stadisticService.deleteStadistic(id);
            return isDeleted ? ResponseEntity.ok("Stadistic deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

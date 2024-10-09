package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Wall;
import co.profiland.co.service.WallService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/Walls") 
public class WallController {
    private final WallService wallService;

    public WallController(WallService wallService) {
        this.wallService = wallService;
    }

    // Create: Save a new Wall
    @PostMapping("/save")
    public ResponseEntity<Wall> saveWall(@RequestBody Wall wall,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Wall savedWall = wallService.saveWall(wall, format);
            return ResponseEntity.ok(savedWall);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Walls
    @GetMapping("/")
    public ResponseEntity<String> getAllWalls() {
        try {
            List<Wall> walls = wallService.getAllWallsMerged();
            return ResponseEntity.ok(wallService.convertToJson(walls));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Wall> getWallById(@RequestParam("id") String id) {
        try {
            Wall Wall = wallService.findWallById(id);
            return Wall != null ? ResponseEntity.ok(Wall) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wall> updateWall(@PathVariable String id,
                                               @RequestBody Wall wall,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Wall updatedWall = wallService.updateWall(id, wall, format);
            return updatedWall != null ? ResponseEntity.ok(updatedWall) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Wall by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWall(@PathVariable String id) {
        try {
            boolean isDeleted = wallService.deleteWall(id);
            return isDeleted ? ResponseEntity.ok("Wall deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

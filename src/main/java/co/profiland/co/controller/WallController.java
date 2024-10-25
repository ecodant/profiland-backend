package co.profiland.co.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Wall;
import co.profiland.co.service.WallService;

import java.util.List;

@RestController
@RequestMapping("/profiland/walls")
public class WallController {

    private final WallService wallService;

    public WallController(WallService wallService) {
        this.wallService = wallService;
    }

    @PostMapping("/")
    public ResponseEntity<Wall> createWall(@RequestBody Wall wall) {
        try {
            Wall savedWall = wallService.createWall(wall);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWall);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Wall>> getAllWalls() {
        try {
            List<Wall> walls = wallService.getAllWalls();
            return ResponseEntity.ok(walls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wall> getWallById(@PathVariable String id) {
        try {
            Wall wall = wallService.findWallById(id);
            return wall != null ? ResponseEntity.ok(wall) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Wall> getWallByOwnerId(@PathVariable String ownerId) {
        try {
            Wall wall = wallService.findWallByOwnerId(ownerId);
            return wall != null ? ResponseEntity.ok(wall) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/owner/{ownerId}/posts")
    public ResponseEntity<List<String>> getPostReferencesByOwnerId(@PathVariable String ownerId) {
        try {
            List<String> postReferences = wallService.getPostReferencesByOwnerId(ownerId);
            return ResponseEntity.ok(postReferences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wall> updateWall(@PathVariable String id, @RequestBody Wall wall) {
        try {
            Wall updatedWall = wallService.updateWall(id, wall);
            return updatedWall != null ? ResponseEntity.ok(updatedWall) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWall(@PathVariable String id) {
        try {
            boolean deleted = wallService.deleteWall(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
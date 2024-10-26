package co.profiland.co.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Wall;
import co.profiland.co.service.WallService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/profiland/walls")
public class WallController {
    
    private final WallService wallService;
    
    @Autowired
    public WallController(WallService wallService) {
        this.wallService = wallService;
    }
    
    @PostMapping("/")
    public ResponseEntity<Wall> createWall(@RequestBody Wall wall) {
        try {
            Wall createdWall = wallService.createWall(wall).get();
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWall);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/")
    public ResponseEntity<List<Wall>> getAllWalls() {
        try {
            List<Wall> walls = wallService.getAllWalls().get();
            return ResponseEntity.ok(walls);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Wall> getWallById(@PathVariable String id) {
        try {
            Wall wall = wallService.findWallById(id).get();
            return wall != null ? 
                ResponseEntity.ok(wall) : 
                ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Wall> getWallByOwnerId(@PathVariable String ownerId) {
        try {
            Wall wall = wallService.findWallByOwnerId(ownerId).get();
            return wall != null ? 
                ResponseEntity.ok(wall) : 
                ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/owner/{ownerId}/posts")
    public ResponseEntity<List<String>> getPostReferences(@PathVariable String ownerId) {
        try {
            List<String> posts = wallService.getPostReferencesByOwnerId(ownerId).get();
            return ResponseEntity.ok(posts);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Wall> updateWall(@PathVariable String id, @RequestBody Wall wall) {
        try {
            Wall updatedWall = wallService.updateWall(id, wall).get();
            return updatedWall != null ? 
                ResponseEntity.ok(updatedWall) : 
                ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWall(@PathVariable String id) {
        try {
            boolean deleted = wallService.deleteWall(id).get();
            return deleted ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.notFound().build();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
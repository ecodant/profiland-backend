package co.profiland.co.service;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import java.util.logging.Level;
import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.model.Wall;
import co.profiland.co.utils.Utilities;

@Service
public class WallService {
    
    private static final String XML_PATH = "src/main/resources/walls/walls.xml";
    private final Utilities persistence;
    private final ThreadPoolManager threadPool;

    public WallService(Utilities persistence) {
        this.persistence = persistence;
        this.threadPool = ThreadPoolManager.getInstance();
        try {
            persistence.initializeFile(XML_PATH, new ArrayList<Wall>());
        } catch (BackupException | PersistenceException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Wall> createWall(Wall wall) {
        return threadPool.submitTask(() -> {
            List<Wall> walls = getAllWalls().get();

            if (wall.getId() == null || wall.getId().isEmpty()) {
                wall.setId(UUID.randomUUID().toString());
            }
            walls.add(wall);

            persistence.serializeObject(XML_PATH, walls);
            persistence.writeIntoLogger("Wall with ID " + wall.getId() + " was created", Level.FINE);
            return wall;
        });
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<List<Wall>> getAllWalls() {
        return threadPool.submitTask(() -> {
            Object deserializedData = persistence.deserializeObject(XML_PATH);
            if (deserializedData instanceof List<?>) {
                persistence.writeIntoLogger("Retrieved all walls successfully", Level.FINE);
                return (List<Wall>) deserializedData;
            }
            return new ArrayList<>();
        });
    }

    public CompletableFuture<Wall> updateWall(String id, Wall updatedWall) {
        return threadPool.submitTask(() -> {
            List<Wall> walls = getAllWalls().get();

            for (int i = 0; i < walls.size(); i++) {
                if (walls.get(i).getId().equals(id)) {
                    updatedWall.setId(id);
                    walls.set(i, updatedWall);
                    persistence.serializeObject(XML_PATH, walls);
                    persistence.writeIntoLogger("Wall with ID " + id + " was updated", Level.FINE);
                    return updatedWall;
                }
            }
            persistence.writeIntoLogger("Wall with ID " + id + " not found for update", Level.WARNING);
            return null;
        });
    }

    public CompletableFuture<Boolean> deleteWall(String id) {
        return threadPool.submitTask(() -> {
            List<Wall> walls = getAllWalls().get();
            boolean removed = walls.removeIf(wall -> wall.getId().equals(id));

            if (removed) {
                persistence.serializeObject(XML_PATH, walls);
                persistence.writeIntoLogger("Wall with ID " + id + " was deleted", Level.FINE);
            } else {
                persistence.writeIntoLogger("Wall with ID " + id + " not found for deletion", Level.WARNING);
            }

            return removed;
        });
    }

    public CompletableFuture<Wall> findWallById(String id) {
        return threadPool.submitTask(() -> {
            Wall wall = getAllWalls().get().stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElse(null);
                
            if (wall != null) {
                persistence.writeIntoLogger("Wall with ID " + id + " was found", Level.FINE);
            } else {
                persistence.writeIntoLogger("Wall with ID " + id + " not found", Level.WARNING);
            }
            return wall;
        });
    }

    public CompletableFuture<Wall> findWallByOwnerId(String ownerId) {
        return threadPool.submitTask(() -> {
            Wall wall = getAllWalls().get().stream()
                .filter(w -> w.getIdOwnerSeller().equals(ownerId))
                .findFirst()
                .orElse(null);
                
            if (wall != null) {
                persistence.writeIntoLogger("Wall found for owner ID " + ownerId, Level.FINE);
            } else {
                persistence.writeIntoLogger("No wall found for owner ID " + ownerId, Level.WARNING);
            }
            return wall;
        });
    }

    public CompletableFuture<List<String>> getPostReferencesByOwnerId(String ownerId) {
        return threadPool.submitTask(() -> {
            Wall wall = findWallByOwnerId(ownerId).get();
            List<String> posts = wall != null ? wall.getPostsReferences() : new ArrayList<>();
            persistence.writeIntoLogger("Retrieved " + posts.size() + " posts for owner ID " + ownerId, Level.FINE);
            return posts;
        });
    }

    public CompletableFuture<String> convertToJson(List<Wall> walls) {
        return threadPool.submitTask(() -> {
            String json = persistence.convertToJson(walls);
            persistence.writeIntoLogger("Converted " + walls.size() + " walls to JSON", Level.FINE);
            return json;
        });
    }
}
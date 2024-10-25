package co.profiland.co.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import java.util.*;

import co.profiland.co.model.Wall;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WallService {
    
    private static final String XML_PATH = "src/main/resources/walls/walls.xml";
    private final Utilities persistence;

    public WallService(Utilities persistence) {
        this.persistence = persistence;
        persistence.initializeFile(XML_PATH, new ArrayList<Wall>());
    }

    public Wall createWall(Wall wall) throws IOException {
        List<Wall> walls = getAllWalls();

        if (wall.getId() == null || wall.getId().isEmpty()) {
            wall.setId(UUID.randomUUID().toString());
        }
        walls.add(wall);

        persistence.serializeObject(XML_PATH, walls);
        log.info("Saved Wall with ID: {}", wall.getId());
        return wall;
    }

    @SuppressWarnings("unchecked")
    public List<Wall> getAllWalls() throws IOException {
        try {
            Object deserializedData = persistence.deserializeObject(XML_PATH);
            if (deserializedData instanceof List<?>) {
                return (List<Wall>) deserializedData;
            }
        } catch (ClassNotFoundException e) {
            log.error("Failed to deserialize Walls", e);
        }
        return new ArrayList<>();
    }

    public Wall updateWall(String id, Wall updatedWall) throws IOException {
        List<Wall> walls = getAllWalls();

        for (int i = 0; i < walls.size(); i++) {
            if (walls.get(i).getId().equals(id)) {
                updatedWall.setId(id);
                walls.set(i, updatedWall);
                persistence.serializeObject(XML_PATH, walls);
                log.info("Updated Wall with ID: {}", id);
                return updatedWall;
            }
        }
        log.warn("Wall not found for update. ID: {}", id);
        return null;
    }

    public boolean deleteWall(String id) throws IOException {
        List<Wall> walls = getAllWalls();
        boolean removed = walls.removeIf(wall -> wall.getId().equals(id));

        if (removed) {
            persistence.serializeObject(XML_PATH, walls);
            log.info("Deleted Wall with ID: {}", id);
        } else {
            log.warn("Wall not found for deletion. ID: {}", id);
        }

        return removed;
    }

    public Wall findWallById(String id) throws IOException {
        return getAllWalls().stream()
                .filter(wall -> wall.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Wall findWallByOwnerId(String ownerId) throws IOException {
        return getAllWalls().stream()
                .filter(wall -> wall.getIdOwnerSeller().equals(ownerId))
                .findFirst()
                .orElse(null);
    }

    public List<String> getPostReferencesByOwnerId(String ownerId) throws IOException {
        Wall wall = findWallByOwnerId(ownerId);
        return wall != null ? wall.getPostsReferences() : new ArrayList<>();
    }

    public String convertToJson(List<Wall> walls) throws IOException {
        return persistence.convertToJson(walls);
    }
}
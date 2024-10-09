package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.Wall;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WallService {
    
    private static final String XML_PATH = "src/main/resources/walls/walls.xml";
    private static final String DAT_PATH = "src/main/resources/walls/walls.dat";

    private final Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Wall saveWall(Wall wall, String format) throws IOException, ClassNotFoundException {
        List<Wall> walls;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            walls = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, walls);
            } else {
                persistence.serializeObject(DAT_PATH, walls);
            }
        } else {
            // If the file exists, read the existing sellers
            walls = getAllWalls(format);
        }

        walls.add(wall);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, walls);
        } else {
            persistence.serializeObject(DAT_PATH, walls);
        }

        return wall;
    }

    // Retrieve all sellers by merging XML and DAT files 
    public List<Wall> getAllWallsMerged() throws IOException, ClassNotFoundException {
        List<Wall> xmlWalls = getAllWalls("xml");
        List<Wall> datWalls = getAllWalls("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Wall> mergedWalls = new ArrayList<>();

        for (Wall wall : xmlWalls) {
            if (seenIds.add(wall.getId())) {
                mergedWalls.add(wall);
            }
        }

        for (Wall wall : datWalls) {
            if (seenIds.add(wall.getId())) {
                mergedWalls.add(wall);
            }
        }

        return mergedWalls;
    }
    public Wall updateWall(String id, Wall updatedWall, String format) throws IOException, ClassNotFoundException {
        List<Wall> walls = getAllWalls(format);

        for (int i = 0; i < walls.size(); i++) {
            if (walls.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedWall.setId(id);
                walls.set(i, updatedWall);  // Replace existing Wall with updated data
                serializeWalls(format, walls);
                return updatedWall;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteWall(String id) throws IOException, ClassNotFoundException {
        List<Wall> walls = getAllWallsMerged();
        boolean removed = walls.removeIf(wall -> wall.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeWalls(id, walls);
            serializeWalls("xml", walls);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Wall> getAllWalls(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Wall>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeWalls(String format, List<Wall> walls) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, walls);
        } else {
            persistence.serializeObject(DAT_PATH, walls);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Wall> walls) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(walls);
    }

    public Wall findWallById(String id) throws IOException, ClassNotFoundException {
        List<Wall> walls = getAllWallsMerged();
        return walls.stream()
                .filter(wall -> wall.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

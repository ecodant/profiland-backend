package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.Stadistic;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StadisticService {
    
    private static final String XML_PATH = "src/main/resources/stadistics/stadistics.xml";
    private static final String DAT_PATH = "src/main/resources/stadistics/stadistics.dat";

    private final Utilities persistence = Utilities.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Stadistic saveStadistic(Stadistic stadistic, String format) throws IOException, ClassNotFoundException {
        List<Stadistic> stadistics;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            stadistics = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObject(XML_PATH, stadistics);
            } else {
                persistence.serializeObject(DAT_PATH, stadistics);
            }
        } else {
            // If the file exists, read the existing sellers
            stadistics = getAllStadistics(format);
        }

        stadistics.add(stadistic);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObject(XML_PATH, stadistics);
        } else {
            persistence.serializeObject(DAT_PATH, stadistics);
        }

        return stadistic;
    }

    // Retrieve all sellers by merging XML and DAT files 
    public List<Stadistic> getAllStadisticsMerged() throws IOException, ClassNotFoundException {
        List<Stadistic> xmlStadistics = getAllStadistics("xml");
        List<Stadistic> datStadistics = getAllStadistics("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Stadistic> mergedStadistics = new ArrayList<>();

        for (Stadistic stadistic : xmlStadistics) {
            if (seenIds.add(stadistic.getId())) {
                mergedStadistics.add(stadistic);
            }
        }

        for (Stadistic Stadistic : datStadistics) {
            if (seenIds.add(Stadistic.getId())) {
                mergedStadistics.add(Stadistic);
            }
        }

        return mergedStadistics;
    }
    public Stadistic updateStadistic(String id, Stadistic updatedStadistic, String format) throws IOException, ClassNotFoundException {
        List<Stadistic> stadistics = getAllStadistics(format);

        for (int i = 0; i < stadistics.size(); i++) {
            if (stadistics.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedStadistic.setId(id);
                stadistics.set(i, updatedStadistic);  // Replace existing Stadistic with updated data
                serializeStadistics(format, stadistics);
                return updatedStadistic;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteStadistic(String id) throws IOException, ClassNotFoundException {
        List<Stadistic> stadistics = getAllStadisticsMerged();
        boolean removed = stadistics.removeIf(stadistic -> stadistic.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeStadistics(id, stadistics);
            serializeStadistics("xml", stadistics);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Stadistic> getAllStadistics(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObject(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Stadistic>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeStadistics(String format, List<Stadistic> stadistics) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObject(XML_PATH, stadistics);
        } else {
            persistence.serializeObject(DAT_PATH, stadistics);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Stadistic> stadistics) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(stadistics);
    }

    public Stadistic findStadisticById(String id) throws IOException, ClassNotFoundException {
        List<Stadistic> stadistics = getAllStadisticsMerged();
        return stadistics.stream()
                .filter(stadistic -> stadistic.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}


package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.StatsPanel;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatsPanelService {
    
    private static final String XML_PATH = "src/main/resources/statsPanels/statsPanels.xml";
    private static final String DAT_PATH = "src/main/resources/statsPanels/statsPanels.dat";

    private final Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public StatsPanel saveStatsPanel(StatsPanel statsPanel, String format) throws IOException, ClassNotFoundException {
        List<StatsPanel> statsPanels;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            statsPanels = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, statsPanels);
            } else {
                persistence.serializeObject(DAT_PATH, statsPanels);
            }
        } else {
            // If the file exists, read the existing sellers
            statsPanels = getAllStatsPanels(format);
        }

        statsPanels.add(statsPanel);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, statsPanels);
        } else {
            persistence.serializeObject(DAT_PATH, statsPanels);
        }

        return statsPanel;
    }

    // Retrieve all sellers by merging XML and DAT files 
    public List<StatsPanel> getAllStatsPanelsMerged() throws IOException, ClassNotFoundException {
        List<StatsPanel> xmlStatsPanels = getAllStatsPanels("xml");
        List<StatsPanel> datStatsPanels = getAllStatsPanels("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<StatsPanel> mergedStatsPanels = new ArrayList<>();

        for (StatsPanel statsPanel : xmlStatsPanels) {
            if (seenIds.add(statsPanel.getId())) {
                mergedStatsPanels.add(statsPanel);
            }
        }

        for (StatsPanel statsPanel : datStatsPanels) {
            if (seenIds.add(statsPanel.getId())) {
                mergedStatsPanels.add(statsPanel);
            }
        }

        return mergedStatsPanels;
    }
    public StatsPanel updateStatsPanel(String id, StatsPanel updatedStatsPanel, String format) throws IOException, ClassNotFoundException {
        List<StatsPanel> statsPanels = getAllStatsPanels(format);

        for (int i = 0; i < statsPanels.size(); i++) {
            if (statsPanels.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedStatsPanel.setId(id);
                statsPanels.set(i, updatedStatsPanel);  // Replace existing StatsPanel with updated data
                serializeStatsPanels(format, statsPanels);
                return updatedStatsPanel;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteStatsPanel(String id) throws IOException, ClassNotFoundException {
        List<StatsPanel> statsPanels = getAllStatsPanelsMerged();
        boolean removed = statsPanels.removeIf(statsPanel -> statsPanel.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeStatsPanels(id, statsPanels);
            serializeStatsPanels("xml", statsPanels);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<StatsPanel> getAllStatsPanels(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<StatsPanel>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeStatsPanels(String format, List<StatsPanel> statsPanels) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, statsPanels);
        } else {
            persistence.serializeObject(DAT_PATH, statsPanels);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<StatsPanel> statsPanels) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(statsPanels);
    }

    public StatsPanel findStatsPanelById(String id) throws IOException, ClassNotFoundException {
        List<StatsPanel> statsPanels = getAllStatsPanelsMerged();
        return statsPanels.stream()
                .filter(statsPanel -> statsPanel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}


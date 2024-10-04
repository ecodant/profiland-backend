package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import co.profiland.co.model.Seller;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SellerService {

    private static final String XML_PATH = "src/main/resources/sellers/sellers.xml";
    private static final String DAT_PATH = "src/main/resources/sellers/sellers.dat";

    private final Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Seller saveSeller(Seller seller, String format) throws IOException, ClassNotFoundException {
        List<Seller> sellers;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            sellers = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, sellers);
            } else {
                persistence.serializeObject(DAT_PATH, sellers);
            }
        } else {
            // If the file exists, read the existing sellers
            sellers = getAllSellers(format);
        }

        sellers.add(seller);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, sellers);
        } else {
            persistence.serializeObject(DAT_PATH, sellers);
        }

        return seller;
    }
    // Retrieve all sellers by merging XML and DAT files 
    public List<Seller> getAllSellersMerged() throws IOException, ClassNotFoundException {
        List<Seller> xmlSellers = getAllSellers("xml");
        List<Seller> datSellers = getAllSellers("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Seller> mergedSellers = new ArrayList<>();

        for (Seller seller : xmlSellers) {
            if (seenIds.add(seller.getId())) {
                mergedSellers.add(seller);
            }
        }

        for (Seller seller : datSellers) {
            if (seenIds.add(seller.getId())) {
                mergedSellers.add(seller);
            }
        }

        return mergedSellers;
    }

    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Seller> getAllSellers(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Seller>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }

    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Seller> sellers) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(sellers);
    }

    public Seller findSellerById(String id) throws IOException, ClassNotFoundException {
        List<Seller> sellers = getAllSellersMerged();
        return sellers.stream()
                .filter(seller -> seller.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Seller> findSellerByName(String name) throws IOException, ClassNotFoundException {
        List<Seller> sellers = getAllSellersMerged();
        return sellers.stream()
                .filter(seller -> seller.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }
}
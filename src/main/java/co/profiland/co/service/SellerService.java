package co.profiland.co.service;

import java.io.IOException;

import org.springframework.stereotype.Service;


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

    public SellerService(){
        persistence.initializeXmlFile(XML_PATH, new ArrayList<Seller>());
        persistence.initializeXmlFile(DAT_PATH, new ArrayList<Seller>());
    }

    public Seller saveSeller(Seller seller, String format) throws IOException, ClassNotFoundException {

        List<Seller> xmlSellers = getAllSellers("xml");
        List<Seller> datSellers = getAllSellers("dat");

        if ("xml".equalsIgnoreCase(format)) {
            xmlSellers.add(seller);
            persistence.serializeObjectXML(XML_PATH, xmlSellers);
        } else {
            datSellers.add(seller);
            persistence.serializeObject(DAT_PATH, datSellers) ;
        }

        return seller;
    }

    //FOR THE FUTURE, BUT WE CAN UPDATED THE WHOLE OBJECT INSTEAD
    //  public Seller addContact(String sellerId, String contactId) throws IOException, ClassNotFoundException {
    //     Seller seller = findSellerById(sellerId);
    //     if (seller != null && seller.addContact(contactId)) {
    //         saveSeller(seller, "dat"); 
    //     }
    //     return seller;
    // }

    // public Seller removeContact(String sellerId, String contactId) throws IOException, ClassNotFoundException {
    //     Seller seller = findSellerById(sellerId);
    //     if (seller != null && seller.removeContact(contactId)) {
    //         saveSeller(seller, "dat"); 
    //     }
    //     return seller;
    // }


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
    public Seller updateSeller(String id, Seller updatedSeller, String format) throws IOException, ClassNotFoundException {
        List<Seller> sellers = getAllSellers(format);

        for (int i = 0; i < sellers.size(); i++) {
            if (sellers.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedSeller.setId(id);
                sellers.set(i, updatedSeller);  // Replace existing seller with updated data
                serializeSellers(format, sellers);
                return updatedSeller;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteSeller(String id) throws IOException, ClassNotFoundException {
        List<Seller> sellers = getAllSellersMerged();
        boolean removed = sellers.removeIf(seller -> seller.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeSellers("dat", sellers);
            serializeSellers("xml", sellers);
        }

        return removed;
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
    private void serializeSellers(String format, List<Seller> sellers) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, sellers);
        } else {
            persistence.serializeObject(DAT_PATH, sellers);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Seller> sellers) throws IOException {
        return persistence.convertToJson(sellers);
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
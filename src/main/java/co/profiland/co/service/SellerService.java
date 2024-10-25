package co.profiland.co.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import co.profiland.co.utils.Utilities;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.DataNotSave;
import co.profiland.co.exception.InvalidCredentials;
import co.profiland.co.exception.SellerNotFoundException;
import co.profiland.co.model.Seller;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SellerService {

    private final String XML_PATH ="C:/td/persistence/sellers/sellers.xml";
    private final String DAT_PATH ="C:/td/persistence/sellers/sellers.dat";
    private final String LOG_PATH ="C:/td/persistence/log/seller_session.log";
    private final ThreadPoolManager threadPool = ThreadPoolManager.getInstance();
    private final Utilities persistence = Utilities.getInstance();

    public SellerService() {
        persistence.initializeFile(XML_PATH, new ArrayList<Seller>());
        persistence.initializeFile(DAT_PATH, new ArrayList<Seller>());
        Utilities.setupLogger(LOG_PATH);
    }

    public CompletableFuture<Seller> saveSeller(Seller seller, String format) {
        return threadPool.submitTask(() -> {
            List<Seller> sellers = getAllSellers(format);
            sellers.add(seller);
            persistence.serializeObject(format.equalsIgnoreCase("xml") ? XML_PATH : DAT_PATH, sellers);
            //Log Requirement - Second Delivery
            persistence.writeIntoLogger("Seller with ID" + seller.getId() + "was register", Level.FINE);
            return seller;
        });
    }
    
    public CompletableFuture<List<Seller>> getAllSellersMerged() {
        return threadPool.submitTask(() -> {
            List<Seller> xmlSellers = getAllSellers("xml");
            List<Seller> datSellers = getAllSellers("dat");
            
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
        });
    }

    public CompletableFuture<Seller> updateSeller(String id, Seller updatedSeller, String format) {
        return threadPool.submitTask(() -> {
            List<Seller> sellers = getAllSellers(format);
            
            for (int i = 0; i < sellers.size(); i++) {
                if (sellers.get(i).getId().equals(id)) {
                    updatedSeller.setId(id);
                    sellers.set(i, updatedSeller);
                    serializeSellers(format, sellers);
                    //Log Requirement - Second Delivery
                    persistence.writeIntoLogger("Seller with ID" + updatedSeller.getId() + "updated its data",Level.FINE);
                    return updatedSeller;
                }
            }
            //Custom Exeption from the first delivery (The UI and all that stuff)
            throw new SellerNotFoundException("Seller with id " + id + " not found");
        });
    }

    public CompletableFuture<Boolean> deleteSeller(String id) {
        return getAllSellersMerged()
            .thenCompose(sellers -> threadPool.submitTask(() -> {
                boolean removed = sellers.removeIf(seller -> seller.getId().equals(id));
                
                if (removed) {
                    // Parallelize the serialization of both formats
                    CompletableFuture<Void> datFuture = CompletableFuture.runAsync(() -> 
                        serializeSellers("dat", sellers));
                    CompletableFuture<Void> xmlFuture = CompletableFuture.runAsync(() -> 
                        serializeSellers("xml", sellers));
                    
                    // Wait for both operations to complete
                    persistence.writeIntoLogger("Seller with ID" + id + "was deleted sucessfully", Level.FINE);
                    CompletableFuture.allOf(datFuture, xmlFuture).join();
                    return true;
                }
                return false;
            }));
    }
    //Authentication for validate the Sellercito credentials
    public CompletableFuture<Seller> authenticateSeller(String email, String password) {
        return getAllSellersMerged()
            .thenApply(sellers -> {
                try {
                    return sellers.stream()
                        .filter(seller -> seller.getEmail().equals(email))
                        .findFirst()
                        .map(seller -> {
                            if (seller.getPassword().equals(password)) {
                                return seller;
                            } else {
                                try {
                                    throw new InvalidCredentials("Invalid password for email: " + email);
                                } catch (InvalidCredentials e) {
                                    persistence.writeIntoLogger("Invalid Authentication", Level.WARNING);
                                    e.printStackTrace();
                                }
                            }
                            return seller;
                        })
                        .orElseThrow(() -> new InvalidCredentials("No seller found with email: " + email));
                } catch (InvalidCredentials e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            });
    }
    
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Seller> getAllSellers(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObject(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Seller>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }

    private void serializeSellers(String format, List<Seller> sellers) {
        try {
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObject(XML_PATH, sellers);
            } else {
                persistence.serializeObject(DAT_PATH, sellers);
            }
        } catch (IOException e) {
            try {   
                //Custom Exeption
                throw new DataNotSave("Failed to serialize sellers");
            } catch (Exception e1) {
                // TODO: handle exception
            }
        }
    }

    public CompletableFuture<String> convertToJson(List<Seller> sellers) {
        return threadPool.submitTask(() -> persistence.convertToJson(sellers));
    }

    public CompletableFuture<Seller> findSellerById(String id) {

        persistence.writeIntoLogger("Searching Seller by the ID" + id, Level.INFO);
        return getAllSellersMerged()
            .thenApply(sellers -> {
                try {
                    return sellers.stream()
                        .filter(seller -> seller.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new SellerNotFoundException("Seller not found: " + id));
                } catch (SellerNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            });
    }

    public CompletableFuture<List<Seller>> findSellerByName(String name) {
        return getAllSellersMerged()
            .thenApply(sellers -> sellers.stream()
                .filter(seller -> seller.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList()));
    }
}
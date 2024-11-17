package co.profiland.co.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import co.profiland.co.utils.Utilities;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.InvalidCredentials;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.exception.SellerNotFoundException;
import co.profiland.co.model.Review;
import co.profiland.co.model.Seller;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final String XML_PATH ="C:/td/persistence/models/sellers/sellers.xml";
    private final String DAT_PATH ="C:/td/persistence/models/sellers/sellers.dat";
    private final String REVIEWS_PATH ="C:/td/persistence/models/reviews/reviews.xml";

    private final ThreadPoolManager threadPool = ThreadPoolManager.getInstance();
    private final Utilities persistence = Utilities.getInstance();
    private final ProductService productService = new ProductService();
    private final ContactRequestService contactRequestService = new ContactRequestService();

    public SellerService() {
        try {
            persistence.initializeFile(XML_PATH, new ArrayList<Seller>());
            persistence.initializeFile(DAT_PATH, new ArrayList<Seller>());
            persistence.initializeFile(REVIEWS_PATH, new ArrayList<Review>());
        } catch (PersistenceException | BackupException e) {
            ((Throwable) e).printStackTrace();
            throw new RuntimeException("Failed to initialize files");
        }
        
        // Utilities.setupLogger();
    }

    public CompletableFuture<Seller> saveSeller(Seller seller, String format) {
        return threadPool.submitTask(() -> {
            List<Seller> sellers = getAllSellers(format);
            sellers.add(seller);
            persistence.serializeObject(format.equalsIgnoreCase("xml") ? XML_PATH : DAT_PATH, sellers);
            //Log Requirement - Second Delivery
            persistence.writeIntoLogger("Seller with ID " + seller.getId() + " was register", Level.FINE);
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

    public CompletableFuture<Seller> updateSeller(String id, Seller updatedSeller) {
        return threadPool.submitTask(() -> {
            List<Seller> sellersDat = getAllSellers("dat");
            List<Seller> sellersXML= getAllSellers("xml");
            
            for (int i = 0; i < sellersDat.size(); i++) {
                if (sellersDat.get(i).getId().equals(id)) {
                    updatedSeller.setId(id);

                    //Updates the products and Request by its state 
                    productService.organizeAndSerializeProducts(updatedSeller.getProducts());
                    contactRequestService.organizeAndSerializeRequests(updatedSeller.getContactRequests());

                    persistence.serializeObject(REVIEWS_PATH, updatedSeller.getReviews());

                    sellersDat.set(i, updatedSeller);
                    serializeSellers("dat", sellersDat);
                    //Log Requirement - Second Delivery
                    persistence.writeIntoLogger("Seller with ID " + updatedSeller.getId() + " updated its data",Level.FINE);
                    return updatedSeller;
                }
            }
            for (int i = 0; i < sellersXML.size(); i++) {
                if (sellersXML.get(i).getId().equals(id)) {
                    updatedSeller.setId(id);

                    //Updates the products and Request by its state 
                    productService.organizeAndSerializeProducts(updatedSeller.getProducts());
                    contactRequestService.organizeAndSerializeRequests(updatedSeller.getContactRequests());

                    persistence.serializeObject(REVIEWS_PATH, updatedSeller.getReviews());

                    
                    sellersXML.set(i, updatedSeller);
                    serializeSellers("xml", sellersXML);
                    //Log Requirement - Second Delivery
                    persistence.writeIntoLogger("Seller with ID " + updatedSeller.getId() + " updated its data",Level.FINE);
                    return updatedSeller;
                }
            }
            //Custom Exeption from the second delivery (The UI and all that stuff)
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
                    persistence.writeIntoLogger("Seller with ID " + id + " was deleted sucessfully", Level.FINE);
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
                                    throw new InvalidCredentials("Invalid password for email in the Login UI: " + email);
                                } catch (InvalidCredentials e) {
                                    persistence.writeIntoLogger("Invalid Authentication in the Login UI", Level.WARNING);
                                    e.printStackTrace();
                                }
                            }
                            return seller;
                        })
                        .orElseThrow(() -> new InvalidCredentials("No seller found with email: " + email));
                } catch (InvalidCredentials e) {
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
        } catch (PersistenceException e) {
            persistence.writeIntoLogger("Error saving seller models into the files", Level.SEVERE);
            e.printStackTrace();

        }  
    }

    public CompletableFuture<String> convertToJson(List<Seller> sellers) {
        return threadPool.submitTask(() -> persistence.convertToJson(sellers));
    }

    public CompletableFuture<Seller> findSellerById(String id) {

        persistence.writeIntoLogger("Searching Seller by the ID in the Home Page" + id, Level.INFO);
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
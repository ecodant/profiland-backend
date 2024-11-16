package co.profiland.co.service;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.exception.ProductNotFound;
import co.profiland.co.model.Product;
import co.profiland.co.model.State;
import co.profiland.co.utils.Utilities;

@Service
public class ProductService {

    private static final String XML_PATH = "C:/td/persistence/models/products";
    private final String AVAILABLE_PATH = XML_PATH + "/available_products.xml";
    private final String SOLD_PATH = XML_PATH + "/sold_products.xml";
    private final String PUBLISHED_PATH = XML_PATH + "/published_products.xml";
    private final String LOG_PATH = "C:/td/persistence/log/Profiland_Log.log";

    private final ThreadPoolManager threadPool = ThreadPoolManager.getInstance();
    private final Utilities persistence = Utilities.getInstance();
    public ProductService() {
        try {
            persistence.initializeFile(AVAILABLE_PATH, new ArrayList<Product>());
            persistence.initializeFile(SOLD_PATH, new ArrayList<Product>());
            persistence.initializeFile(PUBLISHED_PATH, new ArrayList<Product>());
        } catch (PersistenceException | BackupException e) {
            ((Throwable) e).printStackTrace();
            throw new RuntimeException("Failed to initialize files");
        }
        Utilities.setupLogger(LOG_PATH); 
    }

    
    public CompletableFuture<Product> saveProductOnList(Product product) {
        return threadPool.submitTask(() -> {
            List<Product> products = getProductsList(AVAILABLE_PATH);

            if (product.getId() == null || product.getId().isEmpty()) {
                product.setId(UUID.randomUUID().toString());
            }

            products.add(product);
            persistence.serializeObject(AVAILABLE_PATH, products);
            persistence.writeIntoLogger(
                String.format("Product '%s' from seller '%s' was saved", product.getName(), product.getSellerId()),
                Level.INFO
            );

            return product;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error saving product in the Products Section Tab", Level.SEVERE);
            throw new RuntimeException("Failed to save product", ex);
        });
    }

    
    public CompletableFuture<List<Product>> getAllProducts() {
        return threadPool.submitTask(() -> {
            List<Product> mergedList = new ArrayList<>();
            mergedList.addAll(getProductsList(AVAILABLE_PATH));
            mergedList.addAll(getProductsList(SOLD_PATH));
            mergedList.addAll(getProductsList(PUBLISHED_PATH));

            return mergedList;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error retrieving products", Level.WARNING);
            return new ArrayList<>();
        });
    }

public CompletableFuture<Object> organizeAndSerializeProducts(List<Product> products) {
    return threadPool.submitTask(() -> {
        // Initialize empty lists for each state
        List<Product> availableProducts = new ArrayList<>();
        List<Product> soldProducts = new ArrayList<>();
        List<Product> publishedProducts = new ArrayList<>();

        // Categorize products based on their state
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                switch (product.getState()) {
                    case AVAILABLE:
                        availableProducts.add(product);
                        break;
                    case SOLD:
                        soldProducts.add(product);
                        break;
                    case PUBLISHED:
                        publishedProducts.add(product);
                        break;
                    default:
                        persistence.writeIntoLogger(
                            "Invalid product state found for product ID: " + product.getId(),
                            Level.WARNING
                        );
                }
            }
        }

        // Serialize all lists, regardless of whether they're empty or not
        persistence.serializeObject(AVAILABLE_PATH, availableProducts);
        persistence.serializeObject(SOLD_PATH, soldProducts);
        persistence.serializeObject(PUBLISHED_PATH, publishedProducts);

        persistence.writeIntoLogger(
            String.format("Products organized and serialized - Available: %d, Sold: %d, Published: %d",
                availableProducts.size(),
                soldProducts.size(),
                publishedProducts.size()
            ),
            Level.INFO
        );

        return null;
    }).exceptionally(ex -> {
        persistence.writeIntoLogger("Error organizing and serializing products", Level.SEVERE);
        throw new RuntimeException("Failed to organize and serialize products", ex);
    });
}

    public CompletableFuture<Product> findProductById(String id) {
        return getAllProducts().thenApply(products -> 
            {
                try {
                    return products.stream()
                            .filter(product -> product.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new ProductNotFound("Product not found: " + id));
                } catch (ProductNotFound e) {
                    e.printStackTrace();
                }
                return null;
            }
        ).exceptionally(ex -> {
            persistence.writeIntoLogger("Error finding product in the Tab Product section, ID of the product: " + id, Level.WARNING);
            return null;
        });
    }

    // Update handle state transition
    public CompletableFuture<Product> updateProduct(String id, Product updatedProduct) {
        return getAllProducts().thenApply(products -> {
            for (Product product : products) {
                if (product.getId().equals(id)) {
                    updatedProduct.setId(id);
                    moveProductBetweenLists(updatedProduct);
                    // System.out.println("Product Updated " + updatedProduct);
                    return updatedProduct;
                }
            }
            try {
                throw new ProductNotFound("Product with ID " + id + " not found");
            } catch (ProductNotFound e) {
                e.printStackTrace();
            }
            return updatedProduct;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error updating product in the products Section UI", Level.SEVERE);
            throw new RuntimeException("Failed to update product", ex);
        });
    }

    // Move products between lists based on State enum
    private void moveProductBetweenLists(Product product) {
        State state = product.getState();

        switch (state) {
            case PUBLISHED:
                moveProduct(AVAILABLE_PATH, PUBLISHED_PATH, product);
                break;
            case SOLD:
                moveProduct(PUBLISHED_PATH, SOLD_PATH, product);
                break;
            case AVAILABLE:
            // This case is for handle when the user don't change the initial state
                List<Product> availableProducts = getProductsList(AVAILABLE_PATH);

                for (int i = 0; i < availableProducts.size(); i++) {
                    if (availableProducts.get(i).getId().equals(product.getId())) {
                        // product.setId();
                        availableProducts.set(i, product); 
                        serializeProducts(AVAILABLE_PATH, availableProducts);
                    }
                }
                
                break;
            default:
                persistence.writeIntoLogger("Invalid product state: " + state, Level.WARNING);
                break;
        }
    }

    private void moveProduct(String fromPath, String toPath, Product product) {
        List<Product> fromList = getProductsList(fromPath);
        List<Product> toList = getProductsList(toPath);

        if (fromList.removeIf(p -> p.getId().equals(product.getId()))) {
            serializeProducts(fromPath, fromList);
            toList.add(product);
            serializeProducts(toPath, toList);
        } else {
            persistence.writeIntoLogger("Product not found in source list: " + product.getId(), Level.WARNING);
        }
    }

    public CompletableFuture<Boolean> deleteProduct(String id) {
        return findProductById(id).thenApply(product -> {
            boolean deleted;

            switch (product.getState()) {
                case PUBLISHED:
                    deleted = deleteFromList(PUBLISHED_PATH, id);
                    break;
                case SOLD:
                    deleted = deleteFromList(SOLD_PATH, id);
                    break;
                default:
                    deleted = deleteFromList(AVAILABLE_PATH, id);
                    break;
            }

            if (deleted) {
                persistence.writeIntoLogger("Product with ID '" + id + "' deleted", Level.INFO);
            }
            return deleted;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error deleting product", Level.SEVERE);
            return false;
        });
    }

    // Helper to delete from a specific list
    private boolean deleteFromList(String path, String id) {
        List<Product> products = getProductsList(path);
        boolean removed = products.removeIf(p -> p.getId().equals(id));

        if (removed) {
            serializeProducts(path, products);
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    private List<Product> getProductsList(String path) {
        Object data;
        data = persistence.deserializeObject(path);
        if (data instanceof List<?>) {
            return (List<Product>) data; 
        }
        return  new ArrayList<>();
    }

    public CompletableFuture<List<Product>> findProductsByName(String name) {
    return getAllProducts().thenApply(products -> 
            products.stream()
                    .filter(product -> product.getName().equalsIgnoreCase(name))
                    .collect(Collectors.toList())
            ).exceptionally(ex -> {
            persistence.writeIntoLogger("Error searching for products by name: " + name, Level.WARNING);
            return new ArrayList<>();
        });
    }

    private void serializeProducts(String path, List<Product> products) {
        try {
            persistence.serializeObject(path, products);
        } catch (PersistenceException e) {
            persistence.writeIntoLogger("Error saving the products on the files", Level.SEVERE);
            e.printStackTrace();

        }  
    }
    

}

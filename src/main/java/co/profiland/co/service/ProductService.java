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

    private static final String XML_PATH = "C:/td/persistence/products";
    private final String AVAILABLE_PATH = XML_PATH + "/available_products.xml";
    private final String SOLD_PATH = XML_PATH + "/sold_products.xml";
    private final String PUBLISHED_PATH = XML_PATH + "/published_products.xml";
    private final String LOG_PATH = "C:/td/persistence/log/product_session.log";

    private final ThreadPoolManager threadPool = ThreadPoolManager.getInstance();
    private final Utilities persistence = Utilities.getInstance();
    public ProductService() {
        try {
            persistence.initializeFile(AVAILABLE_PATH, new ArrayList<Product>());
            persistence.initializeFile(SOLD_PATH, new ArrayList<Product>());
            persistence.initializeFile(PUBLISHED_PATH, new ArrayList<Product>());
        } catch (BackupException | PersistenceException e) {
            e.printStackTrace();
        }
        Utilities.setupLogger(LOG_PATH); 
    }

    // Save a product and log the operation
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
            persistence.writeIntoLogger("Error saving product", Level.SEVERE);
            throw new RuntimeException("Failed to save product", ex);
        });
    }

    // Fetch all products (merged from all lists)
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

    // Find a product by ID
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
            persistence.writeIntoLogger("Error finding product by ID: " + id, Level.WARNING);
            return null;
        });
    }

    // Update product and handle state transition
    public CompletableFuture<Product> updateProduct(String id, Product updatedProduct) {
        return getAllProducts().thenApply(products -> {
            for (Product product : products) {
                if (product.getId().equals(id)) {
                    updatedProduct.setId(id);
                    moveProductBetweenLists(updatedProduct);
                    return updatedProduct;
                }
            }
            try {
                throw new ProductNotFound("Product with ID " + id + " not found");
            } catch (ProductNotFound e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return updatedProduct;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error updating product", Level.SEVERE);
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
                // No need to move for available state
                break;
            default:
                persistence.writeIntoLogger("Invalid product state: " + state, Level.WARNING);
                break;
        }
    }

    // Move product between two lists and serialize them
    private void moveProduct(String fromPath, String toPath, Product product) {
        List<Product> fromList = getProductsList(fromPath);
        List<Product> toList = getProductsList(toPath);

        if (fromList.removeIf(p -> p.getId().equals(product.getId()))) {
            persistence.serializeObject(fromPath, fromList);
            toList.add(product);
            persistence.serializeObject(toPath, toList);
        } else {
            persistence.writeIntoLogger("Product not found in source list: " + product.getId(), Level.WARNING);
        }
    }

    // Delete a product by ID
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
            persistence.serializeObject(path, products);
        }
        return removed;
    }

    // Fetch products from a given path
    @SuppressWarnings("unchecked")
    private List<Product> getProductsList(String path) {
        Object data = persistence.deserializeObject(path);
        return (data instanceof List<?>) ? (List<Product>) data : new ArrayList<>();
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

}

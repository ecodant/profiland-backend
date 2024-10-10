package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;


import java.util.*;

import co.profiland.co.model.Product;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private static final String XML_PATH = "src/main/resources/products/products.xml";

    private final Persistence persistence = Persistence.getInstance();

    public ProductService() {
        initializeXmlFile();
    }

    private void initializeXmlFile() {
        File file = new File(XML_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                persistence.serializeObjectXML(XML_PATH, new ArrayList<Product>());
            } catch (IOException e) {
                log.error("Failed to initialize XML file", e);
            }
        }
    }

    public Product saveProduct(Product product) throws IOException {
        List<Product> products = getAllProducts();

        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        products.add(product);

        persistence.serializeObjectXML(XML_PATH, products);
        log.info("Saved product with ID: {}", product.getId());
        return product;
    }

    @SuppressWarnings("unchecked")
    public List<Product> getAllProducts() throws IOException {
        try {
            Object deserializedData = persistence.deserializeObjectXML(XML_PATH);
            if (deserializedData instanceof List<?>) {
                return (List<Product>) deserializedData;
            }
        } catch (ClassNotFoundException e) {
            log.error("Failed to deserialize products", e);
        }
        return new ArrayList<>();
    }

    public Product updateProduct(String id, Product updatedProduct) throws IOException {
        List<Product> products = getAllProducts();

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                updatedProduct.setId(id);
                products.set(i, updatedProduct);
                persistence.serializeObjectXML(XML_PATH, products);
                log.info("Updated product with ID: {}", id);
                return updatedProduct;
            }
        }
        log.warn("Product not found for update. ID: {}", id);
        return null;
    }

    public boolean deleteProduct(String id) throws IOException {
        List<Product> products = getAllProducts();
        boolean removed = products.removeIf(product -> product.getId().equals(id));

        if (removed) {
            persistence.serializeObjectXML(XML_PATH, products);
            log.info("Deleted product with ID: {}", id);
        } else {
            log.warn("Product not found for deletion. ID: {}", id);
        }

        return removed;
    }

    public Product findProductById(String id) throws IOException {
        return getAllProducts().stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> findProductByName(String name) throws IOException {
        return getAllProducts().stream()
                .filter(product -> product.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public String convertToJson(List<Product> products) throws IOException {
        return persistence.convertToJson(products);
    }
}
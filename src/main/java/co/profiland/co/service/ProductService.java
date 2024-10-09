package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.Product;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    
    private static final String XML_PATH = "src/main/resources/products/products.xml";
    private static final String DAT_PATH = "src/main/resources/products/products.dat";

    private final static Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Product saveProduct(Product product, String format) throws IOException, ClassNotFoundException {
        List<Product> products;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            products = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, products);
            } else {
                persistence.serializeObject(DAT_PATH, products);
            }
        } else {
            // If the file exists, read the existing sellers
            products = getAllProducts(format);
        }

        products.add(product);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, products);
        } else {
            persistence.serializeObject(DAT_PATH, products);
        }

        return product;
    }

    // Retrieve all sellers by merging XML and DAT files 
    public List<Product> getAllProductsMerged() throws IOException, ClassNotFoundException {
        List<Product> xmlProducts = getAllProducts("xml");
        List<Product> datProducts = getAllProducts("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Product> mergedProducts = new ArrayList<>();

        for (Product product : xmlProducts) {
            if (seenIds.add(product.getId())) {
                mergedProducts.add(product);
            }
        }

        for (Product product : datProducts) {
            if (seenIds.add(product.getId())) {
                mergedProducts.add(product);
            }
        }

        return mergedProducts;
    }
    public Product updateProduct(String id, Product updatedProduct, String format) throws IOException, ClassNotFoundException {
        List<Product> products = getAllProducts(format);

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedProduct.setId(id);
                products.set(i, updatedProduct);  // Replace existing product with updated data
                serializeProducts(format, products);
                return updatedProduct;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteProduct(String id) throws IOException, ClassNotFoundException {
        List<Product> products = getAllProductsMerged();
        boolean removed = products.removeIf(product -> product.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeProducts(id, products);
            serializeProducts("xml", products);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public static List<Product> getAllProducts(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Product>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeProducts(String format, List<Product> products) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, products);
        } else {
            persistence.serializeObject(DAT_PATH, products);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Product> products) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(products);
    }

    public Product findProductById(String id) throws IOException, ClassNotFoundException {
        List<Product> products = getAllProductsMerged();
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> findProductByName(String name) throws IOException, ClassNotFoundException {
        List<Product> products = getAllProductsMerged();
        return products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }
}


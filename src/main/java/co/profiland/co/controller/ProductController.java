package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Product;
import co.profiland.co.service.ProductService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/products") 
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create: Save a new Product
    @PostMapping("/save")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Product savedProduct = productService.saveProduct(product, format);
            return ResponseEntity.ok(savedProduct);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Products
    @GetMapping("/")
    public ResponseEntity<String> getAllProducts() {
        try {
            List<Product> Products = productService.getAllProductsMerged();
            return ResponseEntity.ok(productService.convertToJson(Products));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Product> getProductById(@RequestParam("id") String id) {
        try {
            Product Product = productService.findProductById(id);
            return Product != null ? ResponseEntity.ok(Product) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-name")
    public ResponseEntity<List<Product>> getProductsByName(@RequestParam("name") String name) {
        try {
            List<Product> Products = productService.findProductByName(name);
            return Products.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(Products);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                               @RequestBody Product product,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Product updatedProduct = productService.updateProduct(id, product, format);
            return updatedProduct != null ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        try {
            boolean isDeleted = productService.deleteProduct(id);
            return isDeleted ? ResponseEntity.ok("Product deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
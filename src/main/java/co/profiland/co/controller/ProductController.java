package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.exception.ProductNotFound;
import co.profiland.co.model.Product;
import co.profiland.co.service.ProductService;
import co.profiland.co.service.SellerService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
@RequestMapping("/profiland/products")
public class ProductController {

    private final ProductService productService;
    private final SellerService sellerService;

    public ProductController(ProductService productService, SellerService sellerService) {
        this.productService = productService;
        this.sellerService = sellerService;
    }

    // Save a Product
    @PostMapping("/")
    public CompletableFuture<ResponseEntity<Product>> saveProduct(@RequestBody Product product) {
        return productService.saveProductOnList(product)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    // Get Product by ID
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<Product>> getProductById(@PathVariable String id) {
        return productService.findProductById(id)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ProductNotFound) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.internalServerError().build();
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Product>> updateProduct(
            @PathVariable String id, @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    // Delete Product by ID
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<? extends Object>> deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id)
                .thenApply(deleted -> deleted
                        ? ResponseEntity.ok("Product deleted successfully")
                        : ResponseEntity.notFound().build())
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    // Get All Products
    @GetMapping("/")
    public CompletableFuture<ResponseEntity<List<Product>>> getAllProducts() {
        return productService.getAllProducts()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    // Get Products by Name
    @GetMapping("/name/{name}")
    public CompletableFuture<ResponseEntity<? extends Object>> getProductsByName(@PathVariable String name) {
        return productService.findProductsByName(name)
                .thenApply(products -> {
                    if (products.isEmpty()) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(products);
                }).exceptionally(ex -> ResponseEntity.internalServerError().build());
    }
}

package co.profiland.co.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.components.LoginRequest;
import co.profiland.co.exception.InvalidCredentials;
import co.profiland.co.exception.SellerNotFoundException;
import co.profiland.co.model.Seller;
import co.profiland.co.service.SellerService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/profiland/sellers")
@Slf4j
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<Seller>> saveSeller(
            @RequestBody Seller seller,
            @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        return sellerService.saveSeller(seller, format)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    logError("Error saving seller", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<Seller>> login(@RequestBody LoginRequest loginRequest) {
        return sellerService.authenticateSeller(loginRequest.getEmail(), loginRequest.getPassword())
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof InvalidCredentials) {
                        log.warn("Login failed for email: {}", loginRequest.getEmail());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                    logError("Error during authentication", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/")
    public CompletableFuture<ResponseEntity<String>> getAllSellers() {
        return sellerService.getAllSellersMerged()
                .thenCompose(sellers -> sellerService.convertToJson(sellers))
                .thenApply(jsonString -> ResponseEntity.ok().body(jsonString))
                .exceptionally(throwable -> {
                    logError("Error fetching all sellers", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @SuppressWarnings("unused")
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<? extends Object>> getSellerById(@PathVariable String id) {
        return sellerService.findSellerById(id)
                .thenApply(seller -> seller != null ? 
                    ResponseEntity.ok().body(seller) : 
                    ResponseEntity.<Seller>notFound().build())
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof SellerNotFoundException) {
                        return ResponseEntity.<Seller>notFound().build();
                    }
                    logError("Error fetching seller by ID", throwable);
                    return ResponseEntity.<Seller>internalServerError().build();
                });
    }

    @SuppressWarnings("unused")
    @GetMapping("/name/{name}")
    public CompletableFuture<ResponseEntity<? extends Object>> getSellersByName(@PathVariable String name) {
        return sellerService.findSellerByName(name)
                .thenApply(sellers -> sellers.isEmpty() ? 
                    ResponseEntity.<List<Seller>>notFound().build() : 
                    ResponseEntity.ok().body(sellers))
                .exceptionally(throwable -> {
                    logError("Error fetching sellers by name", throwable);
                    return ResponseEntity.<List<Seller>>internalServerError().build();
                });
    }

    @SuppressWarnings("unused")
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Seller>> updateSeller(
            @PathVariable String id,
            @RequestBody Seller seller,
            @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        return sellerService.updateSeller(id, seller, format)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof SellerNotFoundException) {
                        return ResponseEntity.<Seller>notFound().build();
                    }
                    logError("Error updating seller", throwable);
                    return ResponseEntity.<Seller>internalServerError().build();
                });
    }

    @SuppressWarnings("unused")
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<? extends Object>> deleteSeller(@PathVariable String id) {
        return sellerService.deleteSeller(id)
                .thenApply(deleted -> deleted ? 
                    ResponseEntity.ok().body("Seller deleted successfully ;D") : 
                    ResponseEntity.<String>notFound().build())
                .exceptionally(throwable -> {
                    logError("Error deleting seller", throwable);
                    return ResponseEntity.<String>internalServerError().build();
                });
    }

    private void logError(String message, Throwable throwable) {
        log.error(message, throwable);
    }
}
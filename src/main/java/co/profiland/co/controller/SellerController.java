package co.profiland.co.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.components.LoginRequest;
import co.profiland.co.exception.InvalidCredentials;
import co.profiland.co.exception.SellerNotFoundException;
import co.profiland.co.model.Seller;
import co.profiland.co.service.SellerService;
import co.profiland.co.utils.Utilities;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;


@RestController
@RequestMapping("/profiland/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final Utilities persistence = Utilities.getInstance();

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
                    persistence.writeIntoLogger("Error saving Seller UI Sign Up", Level.WARNING);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<Seller>> login(@RequestBody LoginRequest loginRequest) {
        return sellerService.authenticateSeller(loginRequest.getEmail(), loginRequest.getPassword())
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof InvalidCredentials) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                    persistence.writeIntoLogger("Error during authentication in the Login Tab", Level.WARNING);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/")
    public CompletableFuture<ResponseEntity<List<Seller>>> getAllSellers() {
        return sellerService.getAllSellersMerged()
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> ResponseEntity.internalServerError().build());
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
                    persistence.writeIntoLogger("Error fetching seller by ID", Level.WARNING);
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
                    return ResponseEntity.<List<Seller>>internalServerError().build();
                });
    }

    @SuppressWarnings("unused")
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<Seller>> updateSeller(
            @PathVariable String id,
            @RequestBody Seller seller, String format) {
        return sellerService.updateSeller(id, seller)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof SellerNotFoundException) {
                        return ResponseEntity.<Seller>notFound().build();
                    }
                    persistence.writeIntoLogger("Error Updating seller on Save Data", Level.WARNING);
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
                    persistence.writeIntoLogger("Error Deleting seller on Delete Button ", Level.WARNING);
                    return ResponseEntity.<String>internalServerError().build();
                });
    }

}
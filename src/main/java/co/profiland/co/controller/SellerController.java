package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Seller;
import co.profiland.co.service.SellerService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/sellers") 
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // Create: Save a new Seller
    @PostMapping("/save")
    public ResponseEntity<Seller> saveSeller(@RequestBody Seller seller,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Seller savedSeller = sellerService.saveSeller(seller, format);
            return ResponseEntity.ok(savedSeller);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all sellers
    @GetMapping("/")
    public ResponseEntity<String> getAllSellers() {
        try {
            List<Seller> sellers = sellerService.getAllSellersMerged();
            return ResponseEntity.ok(sellerService.convertToJson(sellers));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Seller> getSellerById(@RequestParam("id") String id) {
        try {
            Seller seller = sellerService.findSellerById(id);
            return seller != null ? ResponseEntity.ok(seller) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-name")
    public ResponseEntity<List<Seller>> getSellersByName(@RequestParam("name") String name) {
        try {
            List<Seller> sellers = sellerService.findSellerByName(name);
            return sellers.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(sellers);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable String id,
                                               @RequestBody Seller seller,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Seller updatedSeller = sellerService.updateSeller(id, seller, format);
            return updatedSeller != null ? ResponseEntity.ok(updatedSeller) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Seller by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSeller(@PathVariable String id) {
        try {
            boolean isDeleted = sellerService.deleteSeller(id);
            return isDeleted ? ResponseEntity.ok("Seller deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
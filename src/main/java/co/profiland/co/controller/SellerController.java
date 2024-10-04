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
            if (seller != null) {
                return ResponseEntity.ok(seller);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-name")
    public ResponseEntity<List<Seller>> getSellersByName(@RequestParam("name") String name) {
        try {
            List<Seller> sellers = sellerService.findSellerByName(name);
            if (sellers.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(sellers);
            }
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
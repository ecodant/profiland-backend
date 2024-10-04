package co.profiland.co.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Seller;
import co.profiland.co.service.SellerService;

@RestController
@RequestMapping("/profiland/users") 
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @GetMapping("/json-from-xml")
    public ResponseEntity<String> getAllSellersInJsonFromXml() {
        return ResponseEntity.ok(sellerService.getAllSellersInJsonFromXml());
    }

    @GetMapping("/json-from-dat")
    public ResponseEntity<String> getAllSellersInJsonFromDat() {
        return ResponseEntity.ok(sellerService.getAllSellersInJsonFromDat());
    }

    @PostMapping("/")
    public ResponseEntity<Seller> saveSeller(@RequestBody Seller seller,
                                             @RequestParam(name = "format", defaultValue = "xml") String format) {
        boolean saveAsXml = format.equalsIgnoreCase("xml");
        return ResponseEntity.ok(sellerService.saveSeller(seller, saveAsXml));
    }
}
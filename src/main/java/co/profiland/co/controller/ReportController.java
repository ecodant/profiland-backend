package co.profiland.co.controller;

import co.profiland.co.service.ReportService;
import co.profiland.co.service.SellerService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;

@RestController
@RequestMapping("/profiland/reports")
public class ReportController {
    private static final String REPORTS_BASE_PATH = "C:/td/persistence/reports/";
    private final ReportService reportService;
    private final SellerService sellerService;
    
    public ReportController(ReportService reportService, SellerService sellerService) {
        this.reportService = reportService;
        this.sellerService = sellerService;
    }
    
    @GetMapping("/monthly-sales/{sellerId}")
    public ResponseEntity<Resource> downloadMonthlySalesReport(
            @PathVariable String sellerId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            return sellerService.findSellerById(sellerId)
                .thenCompose(seller -> reportService.generateMonthlySalesReport(seller, yearMonth))
                .thenApply(this::createResourceResponse)
                .join();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<Resource> downloadInventoryReport() {
        try {
            return sellerService.getAllSellersMerged()
                .thenCompose(sellers -> reportService.generateSellerInventoryReport(sellers))
                .thenApply(this::createResourceResponse)
                .join();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<Resource> createResourceResponse(String fileName) {
        try {
            Path path = Paths.get(REPORTS_BASE_PATH + fileName);
            Resource resource = new UrlResource(path.toUri());
            
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + fileName + "\"")
                .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Error downloading report", e);
        }
    }
}
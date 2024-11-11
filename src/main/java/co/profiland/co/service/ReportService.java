package co.profiland.co.service;


import co.profiland.co.model.Seller;
import co.profiland.co.model.Product;
import co.profiland.co.model.State;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.concurrent.CompletableFuture;


@Service
public class ReportService {
    private static final String REPORTS_BASE_PATH = "C:/td/persistence/reports/";

    private String createReportHeader(String title, String sellerName, String content) {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("<Título>").append(title).append("\n")
                    .append("<Fecha>Fecha: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n")
                    .append("<Usuario>Reporte realizado por: ").append(sellerName).append("\n\n")
                    .append("Información del reporte:\n")
                    .append(content);
        return reportContent.toString();
    }

    public CompletableFuture<String> generateMonthlySalesReport(Seller seller, YearMonth month) {
        return CompletableFuture.supplyAsync(() -> {
            String fileName = String.format("sales_report_%s_%s_%s.txt", 
                seller.getId(), 
                month.format(DateTimeFormatter.ofPattern("yyyy_MM")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
            
            String filePath = REPORTS_BASE_PATH + fileName;
            
            try {
                File directory = new File(REPORTS_BASE_PATH);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                StringBuilder contentBuilder = new StringBuilder();
                
                // Filter products sold in the specified month
                List<Product> monthlySoldProducts = seller.getProducts().stream()
                    .filter(p -> p.getState() == State.SOLD)
                    .filter(p -> {
                        YearMonth productMonth = YearMonth.from(p.getPublicationDate());
                        return productMonth.equals(month);
                    })
                    .toList();
                
                // Calculate total revenue
                int totalRevenue = monthlySoldProducts.stream()
                    .mapToInt(Product::getPrice)
                    .sum();
                
                contentBuilder.append("Periodo: ").append(month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))).append("\n\n");
                contentBuilder.append("RESUMEN DE VENTAS:\n");
                contentBuilder.append("Total de productos vendidos: ").append(monthlySoldProducts.size()).append("\n");
                contentBuilder.append("Ingresos totales: $").append(totalRevenue).append("\n\n");
                contentBuilder.append("PRODUCTOS VENDIDOS:\n");
                contentBuilder.append("------------------\n\n");
                
                for (Product product : monthlySoldProducts) {
                    contentBuilder.append("ID: ").append(product.getId()).append("\n");
                    contentBuilder.append("Nombre: ").append(product.getName()).append("\n");
                    contentBuilder.append("Código: ").append(product.getCode()).append("\n");
                    contentBuilder.append("Categoría: ").append(product.getCategory()).append("\n");
                    contentBuilder.append("Precio: $").append(product.getPrice()).append("\n");
                    contentBuilder.append("Fecha de venta: ").append(
                        product.getPublicationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ).append("\n");
                    contentBuilder.append("------------------\n");
                }

                String fullReport = createReportHeader(
                    "Reporte de Ventas Mensuales",
                    seller.getName() + " " + seller.getLastName(),
                    contentBuilder.toString()
                );

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(fullReport);
                }
                
                return fileName;
            } catch (IOException e) {
                throw new RuntimeException("Error generating monthly sales report", e);
            }
        });
    }

    public CompletableFuture<String> generateSellerInventoryReport(List<Seller> sellers) {
        return CompletableFuture.supplyAsync(() -> {
            String fileName = String.format("inventory_report_%s.txt", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            String filePath = REPORTS_BASE_PATH + fileName;
            
            try {
                File directory = new File(REPORTS_BASE_PATH);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("INVENTARIO TOTAL DE VENDEDORES\n");
                contentBuilder.append("===========================\n\n");

                for (Seller seller : sellers) {
                    contentBuilder.append("VENDEDOR\n");
                    contentBuilder.append("ID: ").append(seller.getId()).append("\n");
                    contentBuilder.append("Nombre: ").append(seller.getName()).append(" ").append(seller.getLastName()).append("\n\n");
                    
                    // Sold Products
                    List<Product> soldProducts = seller.getProducts().stream()
                        .filter(p -> p.getState() == State.SOLD)
                        .toList();
                    
                    contentBuilder.append("PRODUCTOS VENDIDOS:\n");
                    contentBuilder.append("------------------\n");
                    for (Product product : soldProducts) {
                        contentBuilder.append("- ").append(product.getName())
                                    .append(" (ID: ").append(product.getId()).append(")")
                                    .append(" - $").append(product.getPrice()).append("\n");
                    }
                    contentBuilder.append("\n");
                    
                    // Published Products
                    List<Product> publishedProducts = seller.getProducts().stream()
                        .filter(p -> p.getState() != State.SOLD)
                        .toList();
                    
                    contentBuilder.append("PRODUCTOS PUBLICADOS:\n");
                    contentBuilder.append("-------------------\n");
                    for (Product product : publishedProducts) {
                        contentBuilder.append("- ").append(product.getName())
                                    .append(" (ID: ").append(product.getId()).append(")")
                                    .append(" - $").append(product.getPrice()).append("\n");
                    }
                    contentBuilder.append("\n===========================\n\n");
                }

                String fullReport = createReportHeader(
                    "Reporte de Inventario de Vendedores",
                    "Administrador",
                    contentBuilder.toString()
                );

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(fullReport);
                }
                
                return fileName;
            } catch (IOException e) {
                throw new RuntimeException("Error generating seller inventory report", e);
            }
        });
    }

    public File getReportFile(String fileName) throws IOException {
        File file = new File(REPORTS_BASE_PATH + fileName);
        if (!file.exists()) {
            throw new IOException("Report file not found: " + fileName);
        }
        return file;
    }
}
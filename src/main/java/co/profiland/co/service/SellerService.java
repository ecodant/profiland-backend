package co.profiland.co.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import co.profiland.co.model.Seller;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SellerService {

    private static final String SELLER_DATA_XML = "src/main/resources/seller-data/sellers.xml";
    private static final String SELLER_DATA_DAT = "src/main/resources/seller-data/sellers.dat";

    private final Persistence persistence = Persistence.getInstance();
    private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public String getAllSellersInJsonFromXml() {
        List<Seller> sellers = readSellersFromFile(SELLER_DATA_XML, true);
        try {
            return jsonMapper.writeValueAsString(sellers);
        } catch (IOException e) {
            log.error("Error converting sellers to JSON", e);
            return "[]";
        }
    }

    // Retrieve all sellers from DAT format and return as JSON string
    public String getAllSellersInJsonFromDat() {
        List<Seller> sellers = readSellersFromFile(SELLER_DATA_DAT, false);
        try {
            return jsonMapper.writeValueAsString(sellers);
        } catch (IOException e) {
            log.error("Error converting sellers to JSON", e);
            return "[]";
        }
    }

    // Save a seller in XML or DAT format
    public Seller saveSeller(Seller seller, boolean saveAsXml) {
        List<Seller> sellers = readSellersFromFile(saveAsXml ? SELLER_DATA_XML : SELLER_DATA_DAT, saveAsXml);
        sellers.add(seller);
        writeSellersToFile(sellers, saveAsXml ? SELLER_DATA_XML : SELLER_DATA_DAT, saveAsXml);
        return seller;
    }

    // Reads sellers from file based on format
    @SuppressWarnings("unchecked")
    private List<Seller> readSellersFromFile(String filePath, boolean readAsXml) {
        try {
            return (List<Seller>) (readAsXml ?
                    persistence.deserializeObjectXML(filePath) :
                    persistence.deserializeObject(filePath));
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error reading sellers from file: " + filePath, e);
            return new ArrayList<>();
        }
    }

    //  This method uses the utility class and writes sellers to file based on format (XML or DAT)
    private void writeSellersToFile(List<Seller> sellers, String filePath, boolean saveAsXml) {
        try {
            if (saveAsXml) {
                persistence.serializeObjectXML(filePath, sellers);
            } else {
                persistence.serializeObject(filePath, sellers);
            }
        } catch (IOException e) {
            log.error("Error writing sellers to file: " + filePath, e);
        }
    }
}
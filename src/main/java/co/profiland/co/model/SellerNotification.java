package co.profiland.co.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SellerNotification implements Serializable {
    private String id;
    private String message;
    private String typeOfNotification;
}

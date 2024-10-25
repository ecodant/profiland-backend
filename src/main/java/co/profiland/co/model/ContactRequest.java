package co.profiland.co.model;

import java.io.Serializable;
import lombok.Data;
@Data
public class ContactRequest implements Serializable{
    private String id;
    private String idSeller;
    private String state;

    public ContactRequest(){

    }

    public ContactRequest(Seller seller, StateRequest state){
        this.idSeller = seller.getId();
        this.state = state.toString();
    }


}

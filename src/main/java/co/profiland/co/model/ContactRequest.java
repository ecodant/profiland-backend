package co.profiland.co.model;

import java.io.Serializable;
import lombok.Data;
@Data
public class ContactRequest implements Serializable{
    private String id;
    private String idEmisor;
    private StateRequest state;

    public ContactRequest(){

    }

    public ContactRequest(String idEmisor, String idReciver, StateRequest state){
        this.idEmisor = idEmisor;
        this.state = state;
    }
}

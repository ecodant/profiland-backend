
package co.profiland.co.model;
import java.util.ArrayList;

import lombok.Data;

import java.io.Serializable;

@Data
public class Chat implements Serializable {
    private String id;
    private String reciverSeller;
    private ArrayList<Message> messages;

    public Chat(){
        this.messages = new ArrayList<Message>();
    }

    public Chat(String reciverSeller, ArrayList<Message> messages){
        this.reciverSeller = reciverSeller;
        this.messages = messages;
    }
}


package co.profiland.co.model;
import java.util.ArrayList;

import lombok.Data;

import java.io.Serializable;

@Data
public class Chat implements Serializable {
    private String id;
    private ArrayList<Seller> members;
    private ArrayList<Message> messages;

    public Chat(){
        this.members = new ArrayList<Seller>();
        this.messages = new ArrayList<Message>();
    }

    public Chat(ArrayList<Seller> members, ArrayList<Message> messages){
        this.members = members;
        this.messages = messages;
    }
}

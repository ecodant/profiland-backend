
package co.profiland.co.model;
import java.util.ArrayList;
import java.io.Serializable;

public class Chat implements Serializable {
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

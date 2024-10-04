
package co.profiland.co.model;
import java.util.ArrayList;

public class Chat {
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

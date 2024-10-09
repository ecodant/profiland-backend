package co.profiland.co.mainClasses;

import java.util.ArrayList;

import lombok.Data;

@Data
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

package shared.message;

import java.util.ArrayList;
import java.util.HashMap;

public class ReverseResponseMessage extends Message {

    private static final long serialVersionUID = 1L;

    public String id;

    public HashMap<String, ArrayList<String>> reverse;

    @Override
    public String messageType() {
        return "Reverse Response Message";
    }

    public ReverseResponseMessage(String id, HashMap<String, ArrayList<String>> reverse) {
        this.id = id;
        this.reverse = reverse;
    }

}
package shared.message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Not nessecary to perform on slave, but i do this to test extra internet
 * throughput
 */
public class ReduceMessage extends Message {

    private static final long serialVersionUID = 1L;
    public String id;
    public HashMap<String, ArrayList<String>> words;

    @Override
    public String messageType() {
        return "Reduce Message";
    }

    public ReduceMessage(String id, HashMap<String, ArrayList<String>> words) {
        this.id = id;
        this.words = words;
    }

}

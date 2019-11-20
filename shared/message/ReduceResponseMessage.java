package shared.message;

import java.util.HashMap;

public class ReduceResponseMessage extends Message {

    private static final long serialVersionUID = 1L;
    public String id;
    public HashMap<String, Integer> result;

    @Override
    public String messageType() {
        return "Reduce Response Message";
    }

    public ReduceResponseMessage(String id, HashMap<String, Integer> result) {
        this.id = id;
        this.result = result;
    }

}

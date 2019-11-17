package src.operations;

import java.util.ArrayList;
import java.util.HashMap;

import shared.message.ReverseMessage;

public class Reversing {
    ReverseMessage reverseMessage;

    public Reversing(ReverseMessage reverseMessage) {
        this.reverseMessage = reverseMessage;
    }

    public HashMap<String, ArrayList<String>> reverse() {
        HashMap<String, ArrayList<String>> reversed = new HashMap<>();

        for (String key : reverseMessage.value) {
            if (!reversed.containsKey(key)) {
                reversed.put(key, new ArrayList<String>());
            }
            reversed.get(key).add(reverseMessage.key);
        }
        return reversed;
    }

}
package src.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import shared.message.ReduceMessage;

/**
 * Not nesseccary, i do this to measure network throughput on different
 * environments
 */
public class Reducing {
    ReduceMessage reduceMessage;

    public Reducing(ReduceMessage reduceMessage) {
        this.reduceMessage = reduceMessage;
    }

    public HashMap<String, Integer> reduce() {
        HashMap<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : reduceMessage.words.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }
}
package operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ReverseIndex {
    public static HashMap<String, HashSet<String>> reverseIndex(HashMap<String, ArrayList<String>> mappedValues) {
        HashMap<String, HashSet<String>> inversed = new HashMap<String, HashSet<String>>(mappedValues.size());

        for (Map.Entry<String, ArrayList<String>> entry : mappedValues.entrySet()) {
            for (String key : entry.getValue()) {
                if (!inversed.containsKey(key)) {
                    inversed.put(key, new HashSet<String>());
                }
                inversed.get(key).add(entry.getKey());
            }
        }
        return inversed;
    }
}
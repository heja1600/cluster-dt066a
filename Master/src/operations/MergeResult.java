package operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logs.Logger;
import shared.config.Config;

public class MergeResult {
    public static HashMap<String, Integer> mergeResult(Config config, Logger logger,
            HashMap<String, ArrayList<Integer>> reducedValues) {
        logger.log("Merging results", true);
        HashMap<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : reducedValues.entrySet()) {
            Integer counter = 0;
            for (Integer count : entry.getValue()) {
                counter += count;
            }
            result.put(entry.getKey(), counter);
        }
        logger.log("finished merging results", true);
        return result;
    }
}
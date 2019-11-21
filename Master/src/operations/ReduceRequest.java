package operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logs.Logger;

import shared.config.Config;

public class ReduceRequest {
    private Config config;
    private Logger logger;
    private HashMap<String, ArrayList<String>> reverse;
    private HashMap<String, Integer> result = new HashMap<>();

    public ReduceRequest(Config config, Logger logger, HashMap<String, ArrayList<String>> reverse) {
        this.config = config;
        this.logger = logger;
        this.reverse = reverse;
    }

    public HashMap<String, Integer> send() {
        logger.log("reducing messages ", true);
        for (Map.Entry<String, ArrayList<String>> entry : reverse.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        logger.log("reducing done ", true);
        return result;
    }

}
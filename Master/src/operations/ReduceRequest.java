package operations;

import java.util.HashMap;
import java.util.HashSet;

import listeners.IReduceRequest;
import logs.Logger;
import shared.config.Config;

public class ReduceRequest {
    Config config;
    Logger logger;
    HashMap<String, HashSet<String>> reversedValues;
    IReduceRequest iReduceRequest;

    public ReduceRequest(Config config, Logger logger, HashMap<String, HashSet<String>> reversedValues,
            IReduceRequest iReduceRequest) {
        this.config = config;
        this.logger = logger;
        this.reversedValues = reversedValues;
        this.iReduceRequest = iReduceRequest;
    }

    public void send() {

    }
}
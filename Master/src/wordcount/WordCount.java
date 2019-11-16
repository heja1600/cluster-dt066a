
package wordcount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import listeners.IMapRequest;
import listeners.IReduceRequest;
import logs.Logger;
import operations.MapRequest;
import operations.ReduceRequest;
import operations.ReverseIndex;
import operations.Split;
import shared.config.Config;

public class WordCount implements IMapRequest, IReduceRequest {

    Config config;
    Logger logger;

    private ArrayList<String> content;

    public WordCount(Config config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public void countWords() {
        long startTime = System.currentTimeMillis();

        try {
            content = ImportFile.importFile(logger, config.fileInputPath);
            // Split
            ArrayList<String> lines = Split.split(logger, content, config.linesPerSplit);

            // Request map ordering
            MapRequest mapRequest = new MapRequest(lines, config, logger, this);
            mapRequest.send();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapResponse(HashMap<String, ArrayList<String>> mappedValues) {
        HashMap<String, HashSet<String>> reversedValues = ReverseIndex.reverseIndex(mappedValues);
        ReduceRequest reduceRequest = new ReduceRequest(config, logger, reversedValues, this);
        reduceRequest.send();
    }

    @Override
    public void onReduceResponse(HashMap<String, ArrayList<String>> mappedValues) {

    }
}
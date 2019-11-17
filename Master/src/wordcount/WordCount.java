
package wordcount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import listeners.IMapRequest;
import listeners.IReduceRequest;
import listeners.IReverseIndexRequest;
import logs.Logger;
import operations.MapRequest;
import operations.ReduceRequest;
import operations.ReverseIndex;
import operations.Split;
import shared.config.Config;

public class WordCount implements IMapRequest, IReduceRequest, IReverseIndexRequest {

    Config config;
    Logger logger;
    long startTime = System.currentTimeMillis();
    private ArrayList<String> content;

    public WordCount(Config config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public void countWords() {

        try {
            content = ImportFile.importFile(logger, config.fileInputPath);
            logger.overwriteFile(content.toString(), config.loggerImportPath);
            // Split
            ArrayList<String> lines = Split.split(logger, content, config.linesPerSplit);
            logger.overwriteFile(lines.toString(), config.loggerSplitPath);
            // Request map ordering
            MapRequest mapRequest = new MapRequest(lines, config, logger, this);

            mapRequest.send();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapResponse(HashMap<String, ArrayList<String>> mappedValues) {
        logger.overwriteFile(mappedValues.toString(), config.loggerMapPath);

        ReverseIndex reverseIndex = new ReverseIndex(mappedValues, logger, config, this);
        reverseIndex.send();

    }

    @Override
    public void onReduceResponse(HashMap<String, Integer> reducedValues) {
        logger.overwriteFile(reducedValues.toString(), config.loggerReducePath);
        logger.log("finnished", true);
    }

    @Override
    public void onReverseIndexResponse(HashMap<String, ArrayList<String>> reversedValues) {
        logger.overwriteFile(reversedValues.toString(), config.loggerReversePath);
        ReduceRequest reduceRequest = new ReduceRequest(config, logger, reversedValues, this);
        reduceRequest.send();
    }
}

package wordcount;

import java.util.ArrayList;
import java.util.HashMap;

import logs.Logger;
import operations.MapRequest;
import operations.ReduceRequest;
import operations.ReverseIndex;
import operations.Split;
import shared.config.Config;

public class WordCount {

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
            MapRequest mapRequest = new MapRequest(lines, config, logger);
            HashMap<String, ArrayList<String>> mappedValues = mapRequest.send();
            logger.overwriteFile(mappedValues.toString(), config.loggerMapPath);

            ReverseIndex reverseIndex = new ReverseIndex(mappedValues, logger, config);
            HashMap<String, ArrayList<String>> reversedValues = reverseIndex.send();
            logger.overwriteFile(reversedValues.toString(), config.loggerReversePath);

            ReduceRequest reduceRequest = new ReduceRequest(config, logger, reversedValues);
            HashMap<String, Integer> reducedValues = reduceRequest.send();
            logger.overwriteFile(reducedValues.toString(), config.loggerReducePath);

            logger.log("finnished", true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
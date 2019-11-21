
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
            logger.start();
            logger.startImport();
            content = ImportFile.importFile(logger, config.fileInputPath);
            logger.endImport();
            logger.overwriteFile(content.toString(), config.loggerImportPath);
            // Split
            logger.startSplit();
            ArrayList<String> lines = Split.split(logger, content, config.linesPerSplit);
            logger.endSplit();
            logger.overwriteFile(lines.toString(), config.loggerSplitPath);
            // Request map ordering

            logger.startMap();
            MapRequest mapRequest = new MapRequest(lines, config, logger);
            HashMap<String, ArrayList<String>> mappedValues = mapRequest.send();
            logger.endMap();
            logger.overwriteFile(mappedValues.toString(), config.loggerMapPath);

            logger.startReverse();
            ReverseIndex reverseIndex = new ReverseIndex(mappedValues, logger, config);
            HashMap<String, ArrayList<String>> reversedValues = reverseIndex.send();
            logger.endReverse();
            logger.overwriteFile(reversedValues.toString(), config.loggerReversePath);

            logger.startReduce();
            ReduceRequest reduceRequest = new ReduceRequest(config, logger, reversedValues);
            HashMap<String, Integer> reducedValues = reduceRequest.send();
            logger.endReduce();
            logger.overwriteFile(reducedValues.toString(), config.loggerReducePath);
            logger.finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
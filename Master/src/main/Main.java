package main;

import java.nio.file.Paths;
import java.util.List;

import listeners.IRaspberryAlive;
import logs.Logger;
import network.FindRaspberryPis;
import shared.config.Config;
import shared.other.RaspberryPi;
import wordcount.WordCount;

public class Main implements IRaspberryAlive {
    Config config = new Config();
    Logger logger = new Logger(config);

    public static void main(String[] args) {

        new Main(args);
    }

    // file path
    public Main(String[] args) {

        changeConfig(args);
        /**
         * check if connection is up, will trigger @function onRaspberryPiResponse when
         * finnished
         */
        FindRaspberryPis findRaspberryPis = new FindRaspberryPis(config, logger, this);
        findRaspberryPis.find();
    }

    private void changeConfig(String[] args) {
        if (args.length > 0) {
            this.config.fileInputPath = Paths.get("Data/" + args[0]);
        }
        if (args.length > 1) {
            this.config.linesPerSplit = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            this.config.maxMessageBufferSlave = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            this.config.maxAmountOfReduceSize = Integer.parseInt(args[3]);
        }
    }

    @Override
    public void onRaspberryPiResponse(List<RaspberryPi> aliveRaspberryPis) {

        this.config.slaves = aliveRaspberryPis;

        WordCount wordCount = new WordCount(config, logger);
        wordCount.countWords();

    }
}
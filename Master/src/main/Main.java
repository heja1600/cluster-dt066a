package main;

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
        new Main();
    }

    public Main() {

        /**
         * check if connection is up, will trigger @function onRaspberryPiResponse when
         * finnished
         */
        FindRaspberryPis findRaspberryPis = new FindRaspberryPis(config, logger, this);
        findRaspberryPis.find();
    }

    @Override
    public void onRaspberryPiResponse(List<RaspberryPi> aliveRaspberryPis) {
        this.config.slaves = aliveRaspberryPis;

        WordCount wordCount = new WordCount(config, logger);
        wordCount.countWords();

    }
}
package main;

import java.util.List;

import listeners.IRaspberryAlive;
import logs.Logger;
import network.CheckRaspberryPisUp;
import shared.config.Config;
import shared.other.RaspberryPi;

public class Main implements IRaspberryAlive {
    Config config = new Config();
    Logger logger = new Logger();

    public static void main(String[] args) {
        new Main();
    }

    public Main() {

        /**
         * check if connection is up, will trigger @function onRaspberryPiResponse when
         * finnished
         */
        CheckRaspberryPisUp checkRaspberryPisUp = new CheckRaspberryPisUp(config, logger, this);
        checkRaspberryPisUp.check();
    }

    @Override
    public void onRaspberryPiResponse(List<RaspberryPi> aliveRaspberryPis) {

    }
}
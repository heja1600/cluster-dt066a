package logs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import shared.message.Message;

public class Logger {
    File file;
    FileWriter fr;
    BufferedWriter br;
    long initialTime = System.currentTimeMillis();
    long previousTime = System.currentTimeMillis();

    public Logger() {

        try {
            file = new File("logger.txt");
            fr = new FileWriter(file, true);
            br = new BufferedWriter(fr);
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T extends Message> void log(T message) {

        String log = "%MESSAGELOG% {" + message.messageType() + "} from: "
                + message.raspberryPi.getInetAddress().getHostAddress();
        log(log);
    }

    public void log(String log) {
        writeLog("%DEFAULTLOG% " + log);
    }

    private void writeLog(String log) {
        try {
            long timeElapsed = System.currentTimeMillis() - initialTime;
            long timeElapsedSinceLast = System.currentTimeMillis() - previousTime;

            log = log + " [" + toSeconds(timeElapsed) + "s]" + "[" + toSeconds(timeElapsedSinceLast) + "s]";

            br.write("\n" + log);
        } catch (IOException e) {
            e.printStackTrace();
        }
        previousTime = System.currentTimeMillis();
    }

    private String toSeconds(long ms) {
        return "" + (ms / 1000) + "," + (ms - (ms / 1000));
    }
}
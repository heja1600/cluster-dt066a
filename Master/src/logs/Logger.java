package logs;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import shared.config.Config;
import shared.message.Message;

public class Logger {
    private File file;
    private FileWriter fr;
    private BufferedWriter br;
    private long initialTime = System.currentTimeMillis();
    private long previousTime = System.currentTimeMillis();
    private Config config;

    public Logger(Config config) {
        this.config = config;
        try {

            initMembers(config.loggerPath);
            overwriteFile("", config.loggerPath);

            initMembers(config.loggerImportPath);
            overwriteFile("", config.loggerImportPath);

            initMembers(config.loggerMapPath);
            overwriteFile("", config.loggerMapPath);

            initMembers(config.loggerReducePath);
            overwriteFile("", config.loggerReducePath);

            initMembers(config.loggerResultPath);
            overwriteFile("", config.loggerResultPath);

            initMembers(config.loggerReversePath);
            overwriteFile("", config.loggerReversePath);

            initMembers(config.loggerSplitPath);
            overwriteFile("", config.loggerSplitPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T extends Message> void log(T message, boolean shouldSystemOut) {

        String log = "%MESSAGELOG% {" + message.messageType() + "} from: "
                + message.raspberryPi.getInetAddress().getHostAddress();
        writeLog(log, shouldSystemOut);
    }

    public void log(String log, boolean shouldSystemOut) {
        log = "%DEFAULTLOG% " + log;
        writeLog(log, shouldSystemOut);
    }

    private void writeLog(String log, boolean shouldSystemOut) {

        try {
            long timeElapsed = System.currentTimeMillis() - initialTime;
            long timeElapsedSinceLast = System.currentTimeMillis() - previousTime;

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(config.loggerPath.toString(), true)));
            log = log + " [" + toSeconds(timeElapsed) + "s]" + "[" + toSeconds(timeElapsedSinceLast) + "s]";

            if (shouldSystemOut)
                System.out.println(log);

            out.println(log);
            out.close();
        } catch (IOException e) {
            // exception handling left as an exercise for the reader
        }

        previousTime = System.currentTimeMillis();
    }

    private String toSeconds(long ms) {
        return "" + (ms / 1000) + "," + (ms - (ms / 1000));
    }

    private void initMembers(Path path) {

        try {
            file = new File(path.toString());
            fr = new FileWriter(file, true);
            br = new BufferedWriter(fr);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void overwriteFile(String content, Path path) {
        content = content.replaceAll("(.{100})", "$1\n");
        writeLog("%OVERWRITING% Starting -> " + path.toString(), true);
        try {
            initMembers(path);
            PrintWriter writer = new PrintWriter(file);
            writer.print(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeLog("%OVERWRITING% Finnished ->" + path.toString(), true);
    }
}
package logs;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import shared.config.Config;
import shared.message.Message;

public class Logger {
    private File file;
    private long initialTime = System.currentTimeMillis();
    private long previousTime = System.currentTimeMillis();

    private long startTime;
    private long endTime;

    private long importTime;
    private long splitTime;
    private long mapTime;
    private long reverseTime;
    private long reduceTime;

    private long importTimeEnd;
    private long splitTimeEnd;
    private long mapTimeEnd;
    private long reverseTimeEnd;
    private long reduceTimeEnd;

    private Config config;

    private boolean shouldLog = true;

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

            // initMembers(config.loggerResultPath);
            // overwriteFile("", config.loggerResultPath);

            initMembers(config.loggerReversePath);
            overwriteFile("", config.loggerReversePath);

            initMembers(config.loggerSplitPath);
            overwriteFile("", config.loggerSplitPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dontLog() {
        this.shouldLog = false;
    }

    public <T extends Message> void log(T message, boolean shouldSystemOut) {
        if (!shouldLog)
            return;
        String log = "% MESSAGELOG % {" + message.messageType() + "} from: "
                + message.raspberryPi.getInetAddress().getHostAddress();
        writeLog(log, shouldSystemOut);
    }

    public void log(String log, boolean shouldSystemOut) {
        log = "<= DEFAULTLOG => " + log;
        writeLog(log, shouldSystemOut);
    }

    private void writeLog(String log, boolean shouldSystemOut) {
        if (!shouldLog)
            return;
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

    public void endLog(HashMap<String, Integer> reducedValues) {

        try {

            String totalLog = "";
            Integer totalWords = 0;
            for (Integer value : reducedValues.values())
                totalWords += value;

            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new FileWriter(config.loggerResultPath.toString(), true)));

            String fileSize = ((double) ((int) ((double) config.fileInputPath.toFile().length() / (1024 * 1024)) * 1000)
                    / 1000) + "MB";
            Integer linesOfFile = Files.readAllLines(config.fileInputPath, Charset.forName("UTF-8")).size();

            Integer totalMessages = (linesOfFile / config.linesPerSplit) * 4;

            totalLog += "|" + fileSize + "|" + totalMessages + "|" + linesOfFile + "|" + totalWords + "|"
                    + config.linesPerSplit + "|" + config.messageWindow;

            totalLog += "|" + toSeconds(importTimeEnd - importTime) + "s|" + toSeconds(splitTimeEnd - splitTime);
            totalLog += "s|" + toSeconds(mapTimeEnd - mapTime) + "s|" + toSeconds(reverseTimeEnd - reverseTime);
            totalLog += "s|" + toSeconds(reduceTimeEnd - reduceTime) + "s|" + toSeconds(endTime - startTime) + "s|";

            out.println(totalLog);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String toSeconds(long ms) {
        return "" + (ms / 1000) + "," + (ms - (ms / 1000));
    }

    private void initMembers(Path path) {

        file = new File(path.toString());

    }

    public void overwriteFile(String content, Path path) {
        content = content.replaceAll("(.{100})", "$1\n");
        writeLog("[ Overwriting ] Starting -> " + path.toString(), true);
        try {
            initMembers(path);
            PrintWriter writer = new PrintWriter(file);
            writer.print(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeLog("[ Overwriting ] Finnished ->" + path.toString(), true);
    }

    public void startImport() {
        this.importTime = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER IMPORT STARTED =-- started [" + toSeconds(importTime) + "s]", true);
    }

    public void endImport() {
        importTimeEnd = System.currentTimeMillis() - initialTime - startTime;
        writeLog(
                "-== TIMER IMPORT ENDED =-- started [" + toSeconds(importTime) + "s]" + " ended ["
                        + toSeconds(importTimeEnd) + "s]" + " total [" + toSeconds(importTimeEnd - importTime) + "s]",
                true);
    }

    public void startMap() {
        this.mapTime = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER MAP STARTED =-- started [" + toSeconds(mapTime) + "s]", true);
    }

    public void endMap() {
        mapTimeEnd = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER MAP ENDED =-- started [" + toSeconds(mapTime) + "s]" + " ended [" + toSeconds(mapTimeEnd)
                + "s]" + " total [" + toSeconds(mapTimeEnd - mapTime) + "s]", true);
    }

    public void startReverse() {
        this.reverseTime = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER REVERSE STARTED =-- started [" + toSeconds(reverseTime) + "s]", true);
    }

    public void endReverse() {
        reverseTimeEnd = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER REVERSE ENDED =-- started [" + toSeconds(reverseTime) + "s]" + " ended ["
                + toSeconds(reverseTimeEnd) + "s]" + " total [" + toSeconds(reverseTimeEnd - reverseTime) + "s]", true);
    }

    public void startSplit() {
        this.splitTime = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER SPLIT STARTED =-- started [" + toSeconds(splitTime) + "s]", true);
    }

    public void endSplit() {
        splitTimeEnd = System.currentTimeMillis() - initialTime - startTime;
        writeLog(
                "-== TIMER SPLIT ENDED =-- started [" + toSeconds(splitTime) + "s]" + " ended ["
                        + toSeconds(splitTimeEnd) + "s]" + " total [" + toSeconds(splitTimeEnd - splitTime) + "s]",
                true);
    }

    public void startReduce() {
        this.reduceTime = System.currentTimeMillis() - initialTime - startTime;
        writeLog("-== TIMER REDUCE STARTED =-- started [" + toSeconds(reduceTime) + "s]", true);
    }

    public void endReduce() {
        reduceTimeEnd = System.currentTimeMillis() - initialTime - startTime;
        writeLog(
                "-== TIMER REDUCE ENDED =-- started [" + toSeconds(reduceTime) + "s]" + " ended ["
                        + toSeconds(reduceTimeEnd) + "s]" + " total [" + toSeconds(reduceTimeEnd - reduceTime) + "s]",
                true);
    }

    public void finish() {
        this.endTime = System.currentTimeMillis() - initialTime;
        ;

        // IMPORT
        writeLog(
                "-== FINAL STATS IMPORT =-- started [" + toSeconds(importTime) + "s]" + " ended ["
                        + toSeconds(importTimeEnd) + "s]" + " total [" + toSeconds(importTimeEnd - importTime) + "s]",
                true);

        // SPLIT
        writeLog(
                "-== FINAL STATS SPLIT =-- started [" + toSeconds(splitTime) + "s]" + " ended ["
                        + toSeconds(splitTimeEnd) + "s]" + " total [" + toSeconds(splitTimeEnd - splitTime) + "s]",
                true);

        // MAP
        writeLog("-== FINAL STATS MAP =-- started [" + toSeconds(mapTime) + "s]" + " ended [" + toSeconds(mapTimeEnd)
                + "s]" + " total [" + toSeconds(mapTimeEnd - mapTime) + "s]", true);
        // REVERSE
        writeLog("-== FINAL STATS REVERSE =-- started [" + toSeconds(reverseTime) + "s]" + " ended ["
                + toSeconds(reverseTimeEnd) + "s]" + " total [" + toSeconds(reverseTimeEnd - reverseTime) + "s]", true);

        // REVERSE
        writeLog(
                "-== FINAL STATS REDUCE =-- started [" + toSeconds(reduceTime) + "s]" + " ended ["
                        + toSeconds(reduceTimeEnd) + "s]" + " total [" + toSeconds(reduceTimeEnd - reduceTime) + "s]",
                true);

        // TOTAL
        writeLog("-== FINAL STATS MAP REDUCE =-- started [" + toSeconds(startTime) + "s]" + " ended ["
                + toSeconds(endTime) + "s]" + " total [" + toSeconds(endTime - startTime) + "s]", true);

        writeLog("( LINES PER SPLIT ) " + config.linesPerSplit, true);
        writeLog("( WINDOW SIZE ) " + config.messageWindow, true);
        writeLog("( FILE PATH ) " + config.fileInputPath.toString(), true);

    }

    public void start() {
        this.startTime = System.currentTimeMillis() - initialTime;
    }
}
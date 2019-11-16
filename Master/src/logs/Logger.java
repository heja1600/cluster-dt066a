package logs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import shared.message.AwakeMessage;
import shared.message.Message;

public class Logger {
    File file;

    public Logger() {
        System.out.println("Creating file");
        file = new File("logger.txt");
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
            writer.print("");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public <T extends Message> void log(T message) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String log = "%MESSAGELOG% MessageType: " + message.messageType() + " from: "
                + message.raspberryPi.getInetAddress().getAddress();
        log(log);
    }

    public void log(String log) {
        writeLog("%DEFAULTLOG% " + log);
    }

    private void writeLog(String log) {
        try (BufferedReader reader = new BufferedReader(new StringReader(log));
                PrintWriter writer = new PrintWriter(new FileWriter(file));) {
            reader.lines().forEach(line -> writer.println(line));
        } catch (Exception e) {
            System.out.println("error logging" + e.getStackTrace());
        }
    }
}
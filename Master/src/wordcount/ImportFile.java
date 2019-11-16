package wordcount;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import logs.Logger;

public class ImportFile {
    public static ArrayList<String> importFile(Logger logger, Path path) throws IOException {
        logger.log("Importing file");
        List<String> lines = new ArrayList<String>();
        ArrayList<String> cleanLines = new ArrayList<String>();
        lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        for (String line : lines) {
            // get rid of potentiall empty lines
            if (line.length() != 0) {
                line = line.replaceAll("[^\\p{L}\\p{Z}]", "");
                line = line.trim();
                line = line.toLowerCase();
                if (line.length() != 0) {
                    cleanLines.add(line);
                }
            }
        }
        return cleanLines;
    }
}
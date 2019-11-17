package operations;

import java.util.ArrayList;
import logs.Logger;

public class Split {
    public static ArrayList<String> split(Logger logger, ArrayList<String> content, Integer linesPerSplit) {
        ArrayList<String> splits = new ArrayList<>();
        logger.log("Starting split", true);
        String tmpString = new String();
        for (int i = 0; i < content.size(); i++) {
            tmpString += " " + content.get(i);
            if (i % linesPerSplit == 0) {
                splits.add(tmpString);
                tmpString = new String();
            }
        }
        logger.log(splits.size() + " splits to send in total", true);
        return splits;
    }
}
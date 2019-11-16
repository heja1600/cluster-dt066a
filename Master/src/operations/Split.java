package operations;

import java.util.ArrayList;
import logs.Logger;

public class Split {
    public static ArrayList<String> split(Logger logger, ArrayList<String> content, Integer linesPerSplit) {
        logger.log("Splitting content");
        ArrayList<String> sxList = new ArrayList<String>();
        Integer i = 0;
        ArrayList<String> bloc = new ArrayList<String>();

        for (int k = 0; k < content.size(); k++) {
            String ligne = content.get(k);
            // System.out.println(ligne);
            bloc.add(ligne);
            if (((k + 1) % linesPerSplit) == 0 || k == content.size() - 1) {
                sxList.add("S" + i);
                i += 1;
                bloc = new ArrayList<String>();
            }
        }
        System.out.println("Created " + sxList.size() + " splits to send.");
        logger.log("Splitting content finished");
        return sxList;
    }
}
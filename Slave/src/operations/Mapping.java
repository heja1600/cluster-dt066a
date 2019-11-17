package src.operations;

import java.util.ArrayList;

import shared.message.MappingMessage;

public class Mapping {
    MappingMessage mappingMessage;

    public Mapping(MappingMessage mappingMessage) {
        this.mappingMessage = mappingMessage;
    }

    public ArrayList<String> map() {
        ArrayList<String> mappedWords = new ArrayList<String>();
        try {
            String[] words = mappingMessage.content.split(" |\\,");
            for (String word : words) {
                if (word.length() > 0) {
                    mappedWords.add(word);
                }
            }
        } catch (Exception e) {

        }

        return mappedWords;
    }
}
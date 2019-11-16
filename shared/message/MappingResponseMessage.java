package shared.message;

import java.util.ArrayList;

public class MappingResponseMessage extends Message {

    public String id;
    public ArrayList<String> content;

    @Override
    public String messageType() {
        return "Mapping Response Message";
    }

    public MappingResponseMessage(String id, ArrayList<String> content) {
        this.id = id;
        this.content = content;
    }

}

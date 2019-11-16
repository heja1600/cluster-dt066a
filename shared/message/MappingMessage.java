package shared.message;

import java.util.ArrayList;

public class MappingMessage extends Message {

    public String id;
    public String content;

    @Override
    public String messageType() {
        return "Mapping Message";
    }

    public MappingMessage(String id, String content) {
        this.id = id;
        this.content = content;
    }

}

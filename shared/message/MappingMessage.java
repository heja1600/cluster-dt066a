package shared.message;

import shared.other.RaspberryPi;

public class MappingMessage extends Message {

    private static final long serialVersionUID = 1L;
    public String id;
    public String content;

    @Override
    public String messageType() {
        return "Mapping Message";
    }

    public MappingMessage(String id, String content, RaspberryPi pi) {
        this.id = id;
        this.content = content;
        this.raspberryPi = pi;
    }
}

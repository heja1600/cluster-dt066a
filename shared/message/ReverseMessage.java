package shared.message;

import java.util.ArrayList;

import shared.other.RaspberryPi;

public class ReverseMessage extends Message {

    private static final long serialVersionUID = 1L;

    public String id;
    public ArrayList<String> value;
    public String key;

    @Override
    public String messageType() {
        return "Reverse Message";
    }

    public ReverseMessage(String id, String key, ArrayList<String> value, RaspberryPi pi) {
        this.id = id;
        this.value = value;
        this.key = key;
        this.raspberryPi = pi;
    }

}
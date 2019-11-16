package shared.message;

import java.io.Serializable;

import shared.other.IMessageType;
import shared.other.RaspberryPi;

public abstract class Message implements Serializable, IMessageType {

    private static final long serialVersionUID = 1L;

    public RaspberryPi raspberryPi;

}
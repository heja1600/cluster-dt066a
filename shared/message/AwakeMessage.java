package shared.message;

public class AwakeMessage extends Message {

    private static final long serialVersionUID = 1L;

    @Override
    public String messageType() {
        return "Awake Message";
    }
}

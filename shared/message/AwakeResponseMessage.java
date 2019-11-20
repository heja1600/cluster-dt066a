package shared.message;

public class AwakeResponseMessage extends Message {

    /**
     *
     */
    private static final long serialVersionUID = 8892853455236215239L;

    @Override
    public String messageType() {
        return "Awake Response Message";
    }
}

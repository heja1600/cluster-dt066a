package shared.message;

public class MappingMessage extends Message {

    private static final long serialVersionUID = 1L;
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

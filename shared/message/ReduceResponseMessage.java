package shared.message;

public class ReduceResponseMessage extends Message {

    private static final long serialVersionUID = 1L;
    public String id;
    public Integer count;
    public String word;

    @Override
    public String messageType() {
        return "Reduce Message";
    }

    public ReduceResponseMessage(String id, Integer count, String word) {
        this.id = id;
        this.count = count;
        this.word = word;
    }
}

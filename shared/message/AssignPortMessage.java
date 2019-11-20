package shared.message;

public class AssignPortMessage extends Message {

    /**
     *
     */
    private static final long serialVersionUID = -6826552254622812015L;
    public Integer port;

    public AssignPortMessage(Integer port) {
        this.port = port;
    }

    @Override
    public String messageType() {

        return "Assing Port Message";
    }

}
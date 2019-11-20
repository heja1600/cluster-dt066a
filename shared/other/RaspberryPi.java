package shared.other;

import java.io.Serializable;
import java.net.InetAddress;

public class RaspberryPi implements Serializable {

    private static final long serialVersionUID = 1842712778441696321L;
    private final String user = "pi";
    private final String password = "raspberry";
    private InetAddress inetAddress;

    private Integer port;

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public RaspberryPi(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public RaspberryPi(InetAddress inetAddress, Integer port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
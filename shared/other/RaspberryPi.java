package shared.other;

import java.net.InetAddress;

public class RaspberryPi {

    private final String user = "pi";
    private final String password = "raspberry";
    private InetAddress inetAddress;

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
}
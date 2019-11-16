package shared.config;

import java.net.InetAddress;
import java.util.List;

import shared.other.RaspberryPi;

public class Config {
    public final String Pi01 = "192.168.1.9";
    public final String Pi02 = "192.168.1.11";
    public final String Pi03 = "192.168.1.12";
    public final String Pi04 = "192.168.1.13";

    public RaspberryPi self;

    public final int datagramSocketPort = 9999;

    public Config() {
        try {
            self = new RaspberryPi(InetAddress.getLocalHost());
        } catch (Exception e) {
            e.printStackTrace();
            // handle error if no ip address is available
        }
    }
}
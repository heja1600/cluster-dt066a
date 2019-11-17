package shared.config;

import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import shared.other.RaspberryPi;

public class Config {
    public final String Pi01 = "192.168.1.9";
    public final String Pi02 = "192.168.1.11";
    public final String Pi03 = "192.168.1.12";
    public final String Pi04 = "192.168.1.13";

    public final Integer datagramSocketPort = 9999;
    public final Integer findSlaveTimeout = 200;
    public Integer linesPerSplit = 2;

    public List<RaspberryPi> slaves;
    public RaspberryPi self;

    public Path fileInputPath = Paths.get("Data/data10k.csv");
    public Path loggerPath = Paths.get("LogFiles/logger.txt");
    public Path loggerMapPath = Paths.get("LogFiles/map.txt");
    public Path loggerSplitPath = Paths.get("LogFiles/split.txt");
    public Path loggerReducePath = Paths.get("LogFiles/reduce.txt");
    public Path loggerImportPath = Paths.get("LogFiles/import.txt");
    public Path loggerResultPath = Paths.get("LogFiles/result.txt");
    public Path loggerReversePath = Paths.get("LogFiles/reverse.txt");

    public Config() {
        try {
            self = new RaspberryPi(InetAddress.getLocalHost());
        } catch (Exception e) {
            e.printStackTrace();
            // handle error if no ip address is available
        }
    }
}
package listeners;

import java.util.List;

import shared.other.RaspberryPi;

public interface IRaspberryAlive {
    public void onRaspberryPiResponse(List<RaspberryPi> aliveRaspberryPis);
}
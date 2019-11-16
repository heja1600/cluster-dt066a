package network;

import java.util.ArrayList;
import java.util.List;

import listeners.IRaspberryAlive;
import logs.Logger;
import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.AwakeMessage;
import shared.message.Message;
import shared.other.RaspberryPi;
import shared.other.SetTimeout;

public class CheckRaspberryPisUp implements IMessageReceived {

    private Config config;
    private Logger logger;
    private IRaspberryAlive iRaspberryAlive;
    private List<RaspberryPi> aliveRaspberryPis = new ArrayList<>();

    public CheckRaspberryPisUp(Config config, Logger logger, IRaspberryAlive iRaspberryAlive) {
        this.config = config;
        this.iRaspberryAlive = iRaspberryAlive;
    }

    public void check() {
        // make awake message
        UDPCommunication communication = new UDPCommunication(config, this);
        communication.broadcastMessage(new AwakeMessage());
        SetTimeout.setTimeout(() -> iRaspberryAlive.onRaspberryPiResponse(this.aliveRaspberryPis), 2000);
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof AwakeMessage) {
            this.aliveRaspberryPis.add(message.raspberryPi);
        }
    }
}
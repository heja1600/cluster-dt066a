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

public class FindRaspberryPis implements IMessageReceived {

    private Config config;
    private Logger logger;
    private IRaspberryAlive iRaspberryAlive;
    private List<RaspberryPi> aliveRaspberryPis = new ArrayList<>();

    public FindRaspberryPis(Config config, Logger logger, IRaspberryAlive iRaspberryAlive) {
        this.config = config;
        this.logger = logger;
        this.iRaspberryAlive = iRaspberryAlive;
    }

    public void find() {
        // make awake message
        UDPCommunication communication = new UDPCommunication(config, this);
        communication.broadcastMessage(new AwakeMessage());
        SetTimeout.setTimeout(() -> {
            communication.shutdown();
            this.aliveRaspberryPis.remove(0); // temporary, same ip
            iRaspberryAlive.onRaspberryPiResponse(this.aliveRaspberryPis);
        }, config.findSlaveTimeout);
    }

    @Override
    public void onMessageReceived(Message message) {
        logger.log(message, true);

        if (message instanceof AwakeMessage) {
            this.aliveRaspberryPis.add(message.raspberryPi);
        }
    }
}
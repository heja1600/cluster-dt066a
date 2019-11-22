package network;

import java.util.ArrayList;
import java.util.List;

import listeners.IRaspberryAlive;
import logs.Logger;
import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.AssignPortMessage;
import shared.message.AwakeMessage;
import shared.message.AwakeResponseMessage;
import shared.message.Message;
import shared.other.RaspberryPi;
import shared.other.SetTimeout;

public class FindRaspberryPis implements IMessageReceived {

    private Config config;
    private Logger logger;
    private IRaspberryAlive iRaspberryAlive;
    private List<RaspberryPi> aliveRaspberryPis = new ArrayList<>();
    private UDPCommunication communication;
    private Integer portCounter;

    public FindRaspberryPis(Config config, Logger logger, IRaspberryAlive iRaspberryAlive) {
        this.config = config;
        this.logger = logger;
        this.iRaspberryAlive = iRaspberryAlive;
        this.portCounter = config.mainPort;
    }

    public void find() {
        // make awake message
        communication = new UDPCommunication(config.mainPort, this);
        communication.broadcastMessage(new AwakeMessage());
        SetTimeout.setTimeout(() -> {
            communication.shutdown();
            if (this.aliveRaspberryPis.size() == 0) {
                logger.log("no Raspberry Pis were found", true);
                System.exit(0);
                return;
            }
            iRaspberryAlive.onRaspberryPiResponse(this.aliveRaspberryPis);
        }, config.findSlaveTimeout);
    }

    @Override
    public void onMessageReceived(Message message) {
        logger.log(message, true);
        // only nessecary here
        if (message instanceof AwakeResponseMessage) {

            portCounter++;
            message.raspberryPi.setPort(portCounter);
            this.aliveRaspberryPis.add(message.raspberryPi);

            communication.sendMessage(new AssignPortMessage(portCounter), message.raspberryPi.getInetAddress());
        }
    }
}
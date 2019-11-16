package operations;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import listeners.IMapRequest;
import logs.Logger;
import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.MappingMessage;
import shared.message.MappingResponseMessage;
import shared.message.Message;
import shared.other.RaspberryPi;

public class MapRequest implements IMessageReceived {
    private List<String> splitsToSend;

    private Config config;
    private Logger logger;

    private Integer messageId = 0;
    private int counter = 0;
    private HashMap<String, ArrayList<String>> responses = new HashMap<String, ArrayList<String>>();
    private HashMap<String, String> messagesPending = new HashMap<String, String>();
    private UDPCommunication udpCommunication;
    private IMapRequest iMapRequest;

    public MapRequest(ArrayList<String> splitsToSend, Config config, Logger logger, IMapRequest iMapRequest) {
        this.splitsToSend = Collections.synchronizedList(splitsToSend);
        this.config = config;
        this.logger = logger;
        this.iMapRequest = iMapRequest;
        udpCommunication = new UDPCommunication(config, this);
    }

    public void send() {
        logger.log("Starting to send MapRequests");
        for (RaspberryPi slave : config.slaves)
            addPendingMessage(slave);
        for (RaspberryPi slave : config.slaves)
            sendSplit(slave);
    }

    private void addPendingMessage(RaspberryPi slave) {
        messagesPending.put(slave.getInetAddress().getHostAddress(), splitsToSend.get(0));
        splitsToSend.remove(0);
    }

    private void removePendingMessage(RaspberryPi slave) {
        messagesPending.remove(slave.getInetAddress().getHostAddress());
    }

    private String getId(Integer id) {
        return "OrderMap" + id;
    }

    private void sendSplit(RaspberryPi slave) {
        InetAddress slaveAddress = slave.getInetAddress();
        MappingMessage mm = new MappingMessage(getId(messageId++), messagesPending.get(slaveAddress.getHostAddress()));
        udpCommunication.sendMessage(mm, slave.getInetAddress());
    }

    @Override
    public void onMessageReceived(Message message) {

        if (message instanceof MappingResponseMessage) {
            logger.log(((MappingResponseMessage) message).id);
            MappingResponseMessage mrm = (MappingResponseMessage) message;
            responses.put(mrm.id, mrm.content);
            removePendingMessage(message.raspberryPi);
            // If theres still lines to be sent
            if (!splitsToSend.isEmpty()) {
                addPendingMessage(message.raspberryPi);
                sendSplit(message.raspberryPi);
                // If theres no lines to be sent, and there is no pending messages, terminate
                // the process.
            } else {

                // send callback
                if (messagesPending.isEmpty()) {
                    udpCommunication.shutdown();
                    iMapRequest.onMapResponse(responses);
                }
            }
        }
    }
}
package operations;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import listeners.IReverseIndexRequest;
import logs.Logger;
import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.Message;
import shared.message.ReverseMessage;
import shared.message.ReverseResponseMessage;
import shared.other.RaspberryPi;

public class ReverseIndex implements IMessageReceived {
    private Iterator<Entry<String, ArrayList<String>>> it;
    private Entry<String, ArrayList<String>> current;
    private Logger logger;
    private Config config;
    private UDPCommunication udpCommunication;
    private HashMap<String, ReverseMessage> messagesPending = new HashMap<>();
    private Integer messageId = 0;
    private IReverseIndexRequest iReverseIndexRequest;
    private HashMap<String, ArrayList<String>> reversed;

    public ReverseIndex(HashMap<String, ArrayList<String>> mappedValues, Logger logger, Config config,
            IReverseIndexRequest iReverseIndexRequest) {
        this.it = mappedValues.entrySet().iterator();
        this.iReverseIndexRequest = iReverseIndexRequest;
        this.logger = logger;
        this.config = config;
        this.reversed = new HashMap<String, ArrayList<String>>(mappedValues.size());
        this.udpCommunication = new UDPCommunication(config, this);
    }

    public void send() {
        for (RaspberryPi slave : config.slaves)
            addPendingMessage(slave);
        for (RaspberryPi slave : config.slaves)
            sendReverse(slave);

    }

    private void addPendingMessage(RaspberryPi slave) {
        this.current = this.it.next();
        ReverseMessage reverseMessage = new ReverseMessage(getId(messageId++), current.getKey(), current.getValue());
        messagesPending.put(slave.getInetAddress().getHostAddress(), reverseMessage);

    }

    private void sendReverse(RaspberryPi slave) {
        InetAddress slaveAddress = slave.getInetAddress();
        ReverseMessage rm = messagesPending.get(slaveAddress.getHostAddress());
        udpCommunication.sendMessage(rm, slave.getInetAddress());
    }

    private String getId(Integer id) {
        return "#OM_ID::" + id;
    }

    private void removePendingMessage(RaspberryPi slave) {
        messagesPending.remove(slave.getInetAddress().getHostAddress());
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message instanceof ReverseResponseMessage) {
            ReverseResponseMessage rrm = (ReverseResponseMessage) message;
            logger.log(rrm, false);
            removePendingMessage(message.raspberryPi);
            addNewReverseResponse(rrm.reverse);
            if (it.hasNext()) {
                addPendingMessage(message.raspberryPi);
                sendReverse(message.raspberryPi);
            } else {
                if (messagesPending.isEmpty()) {
                    udpCommunication.shutdown();
                    logger.log("Finished to send Reverse", true);
                    iReverseIndexRequest.onReverseIndexResponse(reversed);
                }
            }
        }
    }

    private void addNewReverseResponse(HashMap<String, ArrayList<String>> reverse) {
        for (Map.Entry<String, ArrayList<String>> entry : reverse.entrySet()) {
            if (!reversed.containsKey(entry.getKey()))
                reversed.put(entry.getKey(), entry.getValue());
            else
                reversed.get(entry.getKey()).addAll(entry.getValue());
        }
    }
}
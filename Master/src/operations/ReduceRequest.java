package operations;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import listeners.IReduceRequest;
import logs.Logger;
import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.Message;
import shared.message.ReduceMessage;
import shared.message.ReduceResponseMessage;
import shared.other.RaspberryPi;

public class ReduceRequest implements IMessageReceived {
    private Config config;
    private Logger logger;

    private UDPCommunication udpCommunication;
    private IReduceRequest iReduceRequest;
    private HashMap<String, ArrayList<String>> reverse;
    private HashMap<String, ArrayList<Integer>> result = new HashMap<>();
    private HashMap<String, ReduceMessage> messagesPending = new HashMap<>();
    private ArrayList<String> keys = new ArrayList<>();
    private Integer messageId = 0;

    public ReduceRequest(Config config, Logger logger, HashMap<String, ArrayList<String>> reverse,
            IReduceRequest iReduceRequest) {
        this.config = config;
        this.logger = logger;
        this.iReduceRequest = iReduceRequest;
        this.reverse = reverse;
        this.udpCommunication = new UDPCommunication(config, this);
    }

    public void send() {
        Integer totalMessages = 0;
        logger.log("send reduces", true);
        for (String key : reverse.keySet()) {
            keys.add(key);
            totalMessages++;
        }
        logger.log("total of " + totalMessages + " ReduceMessage's has to be sent", true);

        for (RaspberryPi slave : config.slaves) {
            logger.log(slave.getInetAddress().getHostAddress() + " is prepared for reduce", true);
            addPendingMessage(slave);
        }

        for (RaspberryPi slave : config.slaves)
            sendReduce(slave);
    }

    private void addPendingMessage(RaspberryPi slave) {

        String key = keys.get(0);
        keys.remove(0);
        ArrayList<String> reduce = reverse.get(key);

        ReduceMessage rm = new ReduceMessage(getId(messageId++), key, reduce);

        messagesPending.put(slave.getInetAddress().getHostAddress(), rm);

    }

    private String getId(Integer id) {
        return "#RM_ID::" + id;
    }

    private void removePendingMessage(RaspberryPi slave) {
        messagesPending.remove(slave.getInetAddress().getHostAddress());
    }

    private void sendReduce(RaspberryPi slave) {
        InetAddress slaveAddress = slave.getInetAddress();
        ReduceMessage reduceMessage = messagesPending.get(slaveAddress.getHostAddress());
        udpCommunication.sendMessage(reduceMessage, slave.getInetAddress());

    }

    private void insert(String word, Integer value) {
        if (result.get(word) == null)
            result.put(word, new ArrayList<Integer>());
        result.get(word).add(value);
    }

    @Override
    public void onMessageReceived(Message message) {

        if (message instanceof ReduceResponseMessage) {
            ReduceResponseMessage rrm = (ReduceResponseMessage) message;
            logger.log(rrm, false);
            insert(rrm.word, rrm.count);
            removePendingMessage(message.raspberryPi);
            // If theres still lines to be sent
            if (!keys.isEmpty()) {
                addPendingMessage(message.raspberryPi);
                sendReduce(message.raspberryPi);
                // If theres no lines to be sent, and there is no pending messages, terminate
                // the process.
            } else {
                // send callback
                if (messagesPending.isEmpty()) {
                    udpCommunication.shutdown();
                    logger.log("Finished with reduce", true);
                    iReduceRequest.onReduceResponse(result);
                }
            }
        }
    }
}
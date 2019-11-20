package network;

import java.util.ArrayList;

import logs.Logger;
import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.message.Message;

public class MessageHandler<T extends Message> extends Thread implements IMessageReceived {
    private UDPCommunication udpCommunication;
    private ArrayList<T> pendingMessages;
    private ArrayList<Message> receivedMessages;
    private Logger logger;
    private boolean runMessageHandler = true;
    private ArrayList<T> windowMessages;

    private Integer messagesRecieved = 0;
    private Integer messagesToBerecieved;

    public MessageHandler(ArrayList<T> pendingMessages, Integer messageWindow, Integer port, Logger logger) {
        this.messagesToBerecieved = pendingMessages.size();
        this.udpCommunication = new UDPCommunication(port, this);
        this.pendingMessages = pendingMessages;
        this.receivedMessages = new ArrayList<Message>();
        this.windowMessages = new ArrayList<T>();
        this.logger = logger;

        logger.log("message handler sending " + pendingMessages.size() + " messages on port " + port, true);
        for (int i = 0; i < messageWindow && !pendingMessages.isEmpty(); i++) {
            windowMessages.add(pendingMessages.get(0));
            pendingMessages.remove(0);
        }

    }

    @Override
    public void run() {
        for (T message : windowMessages)
            udpCommunication.sendMessage(message, message.raspberryPi.getInetAddress());
        Integer counter = 0;
        while (runMessageHandler) {
            if (counter > 100000)
                counter = 0;
            counter++; // to keep event stack busy, otherwise it will lock
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        logger.log(message, true);
        receivedMessages.add(message);
        messagesRecieved++;
        if (pendingMessages.size() > 0) {
            Message m = pendingMessages.get(0);
            pendingMessages.remove(0);
            udpCommunication.sendMessage(m, message.raspberryPi.getInetAddress());
        } else if (messagesRecieved == messagesToBerecieved) {
            this.udpCommunication.shutdown();
            this.runMessageHandler = false;
        }
    }

    public ArrayList<Message> getMessages() {
        return receivedMessages;
    }

}
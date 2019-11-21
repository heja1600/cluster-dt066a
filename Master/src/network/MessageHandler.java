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

    private ArrayList<T> windowMessages;

    private Integer messagesRecieved = 0;
    private Integer messagesToBeRecieved;
    private Integer port;

    public MessageHandler(ArrayList<T> pendingMessages, Integer messageWindow, Integer port, Logger logger) {
        this.messagesToBeRecieved = pendingMessages.size();
        this.udpCommunication = new UDPCommunication(port, this);
        this.pendingMessages = pendingMessages;
        this.receivedMessages = new ArrayList<Message>();
        this.windowMessages = new ArrayList<T>();
        this.logger = logger;
        this.port = port;
        logger.log("message handler sending " + pendingMessages.size() + ", port " + port + ", window size "
                + messageWindow, true);
        for (int i = 0; i < messageWindow && !pendingMessages.isEmpty(); i++) {
            windowMessages.add(pendingMessages.get(0));
            pendingMessages.remove(0);
        }
    }

    @Override
    public void run() {
        if (!windowMessages.isEmpty()) {

            for (T message : windowMessages)
                udpCommunication.sendMessage(message, message.raspberryPi.getInetAddress());

            try {
                udpCommunication.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        } else {
            this.udpCommunication.shutdown();
        }

    }

    @Override
    public void onMessageReceived(Message message) {
        logger.log(message, false);
        receivedMessages.add(message);
        messagesRecieved++;
        if (pendingMessages.size() > 0) {
            Message m = pendingMessages.get(0);
            pendingMessages.remove(0);
            udpCommunication.sendMessage(m, message.raspberryPi.getInetAddress());
        } else if (messagesRecieved.equals(messagesToBeRecieved)) {
            this.udpCommunication.shutdown();
            logger.log(
                    "message handler finished on port " + port + " with  total of " + messagesToBeRecieved + "messages",
                    true);
        }
    }

    public ArrayList<Message> getMessages() {
        return receivedMessages;
    }

}
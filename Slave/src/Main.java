package src;

import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.Message;

public class Main implements IMessageReceived {
    Config config;
    UDPCommunication udpCommunication;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        config = new Config();
        udpCommunication = new UDPCommunication(config, this);
    }

    @Override
    public void onMessageReceived(Message message) {
        // TODO Auto-generated method stub

    }

}
package src;

import java.util.ArrayList;

import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.AwakeMessage;
import shared.message.MappingMessage;
import shared.message.MappingResponseMessage;
import shared.message.Message;
import shared.message.ReduceMessage;
import src.operations.Mapping;
import src.operations.Reducing;

public class Main implements IMessageReceived {
    Config config;
    UDPCommunication udpCommunication;
    boolean awakeSent = false;
    int counter = 0;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        config = new Config();
        udpCommunication = new UDPCommunication(config, this);
    }

    @Override
    public void onMessageReceived(Message message) {

        if (message instanceof AwakeMessage && !awakeSent) {
            awakeSent = true;
            udpCommunication.sendMessage(new AwakeMessage(), message.raspberryPi.getInetAddress());

        } else if (message instanceof MappingMessage) {
            handleMappingMessage((MappingMessage) message);

        } else if (message instanceof ReduceMessage) {
            handleReduceMessage((ReduceMessage) message);

        }
    }

    public void handleMappingMessage(MappingMessage message) {
        counter++;
        System.out.println(counter);
        MappingMessage mm = (MappingMessage) message;
        Mapping mapping = new Mapping(mm);
        ArrayList<String> words = mapping.map();

        MappingResponseMessage mrm = new MappingResponseMessage(message.id, words);
        udpCommunication.sendMessage(mrm, message.raspberryPi.getInetAddress());
        System.out.println(message.id);
    }

    public void handleReduceMessage(ReduceMessage message) {
        Reducing reducing = new Reducing(message);
        reducing.reduce();
    }
}
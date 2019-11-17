package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.AwakeMessage;
import shared.message.MappingMessage;
import shared.message.MappingResponseMessage;
import shared.message.Message;
import shared.message.ReduceMessage;
import shared.message.ReduceResponseMessage;
import shared.message.ReverseMessage;
import shared.message.ReverseResponseMessage;
import src.operations.Mapping;
import src.operations.Reducing;
import src.operations.Reversing;

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

        } else if (message instanceof ReverseMessage) {
            handleReverseMessage((ReverseMessage) message);
        }
    }

    public void handleMappingMessage(MappingMessage message) {
        Mapping mapping = new Mapping(message);
        ArrayList<String> words = mapping.map();

        MappingResponseMessage mrm = new MappingResponseMessage(message.id, words);
        udpCommunication.sendMessage(mrm, message.raspberryPi.getInetAddress());
    }

    public void handleReduceMessage(ReduceMessage message) {
        Reducing reducing = new Reducing(message);
        HashMap<String, Integer> reduced = reducing.reduce();

        ReduceResponseMessage rrm = new ReduceResponseMessage(message.id, reduced);
        udpCommunication.sendMessage(rrm, message.raspberryPi.getInetAddress());
    }

    public void handleReverseMessage(ReverseMessage message) {
        Reversing reversing = new Reversing(message);
        HashMap<String, ArrayList<String>> reversed = reversing.reverse();

        ReverseResponseMessage rrm = new ReverseResponseMessage(message.id, reversed);
        udpCommunication.sendMessage(rrm, message.raspberryPi.getInetAddress());

    }
}
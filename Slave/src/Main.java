package src;

import java.util.ArrayList;
import java.util.HashMap;

import shared.communication.UDPCommunication;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.AssignPortMessage;
import shared.message.AwakeMessage;
import shared.message.AwakeResponseMessage;
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
    UDPCommunication privateCommunication;
    UDPCommunication globalCommunication;
    int sendCounter = 1;
    boolean shouldLog = false;
    Integer privatePort;

    public static void main(String[] args) {

        System.out.println("running slave");
        new Main(args);
    }

    public Main(String[] args) {
        if (args.length > 0) {
            shouldLog = true;
            System.out.println("Logging mode activated");
        }

        config = new Config();
        globalCommunication = new UDPCommunication(config.mainPort, this);

    }

    @Override
    public void onMessageReceived(Message message) {

        System.out.println(message.messageType());
        if (shouldLog) {
            System.out
                    .println("Recieving {" + message.messageType() + "} from " + message.raspberryPi.getInetAddress());
        }
        if (message instanceof AssignPortMessage) {
            handleAssignPortMessage((AssignPortMessage) message);
        }
        if (message instanceof AwakeMessage) {
            System.out.println("awake");
            globalCommunication.sendMessage(new AwakeResponseMessage(), message.raspberryPi.getInetAddress());

        } else if (message instanceof MappingMessage) {
            handleMappingMessage((MappingMessage) message);

        } else if (message instanceof ReduceMessage) {
            handleReduceMessage((ReduceMessage) message);

        } else if (message instanceof ReverseMessage) {
            handleReverseMessage((ReverseMessage) message);
        }
    }

    public void handleAssignPortMessage(AssignPortMessage apm) {
        sendCounter = 0;
        this.privatePort = apm.port;
        System.out.println("recieved new private communication link in port " + apm.port);
        if (this.privateCommunication != null)
            this.privateCommunication.shutdown();
        this.privateCommunication = new UDPCommunication(apm.port, this);
    }

    public void handleMappingMessage(MappingMessage message) {
        Mapping mapping = new Mapping(message);
        ArrayList<String> words = mapping.map();

        MappingResponseMessage mrm = new MappingResponseMessage(message.id, words);

        if (shouldLog) {
            System.out.println(sendCounter++ + " msg:s sent, Sending {" + mrm.messageType() + "} to "
                    + message.raspberryPi.getInetAddress() + "on private port " + this.privatePort);
        }
        privateCommunication.sendMessage(mrm, message.raspberryPi.getInetAddress());
    }

    public void handleReduceMessage(ReduceMessage message) {
        Reducing reducing = new Reducing(message);
        HashMap<String, Integer> reduced = reducing.reduce();

        ReduceResponseMessage rrm = new ReduceResponseMessage(message.id, reduced);
        if (shouldLog) {
            System.out.println(sendCounter++ + " msg:s sent, Sending {" + rrm.messageType() + "} to "
                    + message.raspberryPi.getInetAddress() + "on private port " + this.privatePort);
        }
        privateCommunication.sendMessage(rrm, message.raspberryPi.getInetAddress());
    }

    public void handleReverseMessage(ReverseMessage message) {
        Reversing reversing = new Reversing(message);
        HashMap<String, ArrayList<String>> reversed = reversing.reverse();

        ReverseResponseMessage rrm = new ReverseResponseMessage(message.id, reversed);

        if (shouldLog) {
            System.out.println(sendCounter++ + " msg:s sent, Sending {" + rrm.messageType() + "} to "
                    + message.raspberryPi.getInetAddress() + "on private port " + this.privatePort);
        }
        privateCommunication.sendMessage(rrm, message.raspberryPi.getInetAddress());

    }
}
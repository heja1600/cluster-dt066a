package shared.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import shared.message.MessageSerializer;
import shared.other.RaspberryPi;
import shared.communication.listeners.IMessageReceived;
import shared.config.Config;
import shared.message.*;

public class UDPCommunication {
    private DatagramSocket datagramSocket = null;
    public boolean runCommunication = true;
    private MessageSerializer messageSerializer = new MessageSerializer();
    private Config config;
    private IMessageReceived iMessageReceived;

    public UDPCommunication(Config config, IMessageReceived iMessageReceived) {
        this.iMessageReceived = iMessageReceived;
        this.config = config;
        try {
            runCommunication = true;
            datagramSocket = new MulticastSocket(config.datagramSocketPort);
            RecieveThread rt = new RecieveThread();
            rt.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.runCommunication = false;
    }

    public <T extends Message> void broadcastMessage(T message) {
        try {
            sendMessage(message, InetAddress.getByName("255.255.255.255"));
        } catch (Exception e) {
            // handle error;
        }
    }

    public <T extends Message> void sendMessage(T message, InetAddress ipAddres) {
        try {
            byte[] sendData = messageSerializer.serializeMessage(message);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddres,
                    config.datagramSocketPort);
            datagramSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RecieveThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[65536];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

            while (runCommunication) {
                try {
                    datagramSocket.receive(datagramPacket);
                    Message message = attachRaspberryPi(datagramPacket);
                    handleMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public Message attachRaspberryPi(DatagramPacket datagramPacket) {
            byte[] packetData = datagramPacket.getData();
            Message recievedMessage = messageSerializer.deserializeMessage(packetData);
            recievedMessage.raspberryPi = new RaspberryPi(datagramPacket.getAddress());
            return recievedMessage;
        }

        public void handleMessage(Message message) {
            if (message.raspberryPi.getInetAddress().getHostAddress() != config.self.getInetAddress().getHostAddress())
                iMessageReceived.onMessageReceived(message);
        }
    }
}
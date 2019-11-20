package shared.communication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import shared.message.MessageSerializer;
import shared.other.RaspberryPi;
import shared.communication.listeners.IMessageReceived;
import shared.message.*;

public class UDPCommunication {
    private DatagramSocket datagramSocket = null;
    private MessageSerializer messageSerializer = new MessageSerializer();
    private IMessageReceived iMessageReceived;
    private RecieveThread rt;
    private Integer port;
    public boolean runCommunication = true;

    public UDPCommunication(Integer port, IMessageReceived iMessageReceived) {
        this.iMessageReceived = iMessageReceived;
        this.port = port;
        try {
            datagramSocket = new MulticastSocket(port);
            rt = new RecieveThread();
            rt.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void join() throws InterruptedException {
        rt.join();
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

    public <T extends Message> void sendMessage(T message, InetAddress inetAddress) {
        try {
            byte[] sendData = messageSerializer.serializeMessage(message);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, port);
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
            datagramSocket.close();
        }

        public Message attachRaspberryPi(DatagramPacket datagramPacket) {
            byte[] packetData = datagramPacket.getData();
            Message recievedMessage = messageSerializer.deserializeMessage(packetData);

            recievedMessage.raspberryPi = new RaspberryPi(datagramPacket.getAddress());
            return recievedMessage;
        }

        public void handleMessage(Message message) {
            iMessageReceived.onMessageReceived(message);
        }
    }

}
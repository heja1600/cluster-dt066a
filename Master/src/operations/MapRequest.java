package operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import logs.Logger;
import network.MessageHandler;

import shared.config.Config;
import shared.message.MappingMessage;
import shared.message.MappingResponseMessage;
import shared.message.Message;

public class MapRequest {
    private List<String> splitsToSend;

    private Config config;
    private Logger logger;

    private Integer messageSequenceCounter = 0;
    private HashMap<String, ArrayList<String>> responses;
    private ArrayList<MessageHandler<MappingMessage>> messageHandlers;

    private ArrayList<ArrayList<MappingMessage>> messages = new ArrayList<>();

    public MapRequest(ArrayList<String> splitsToSend, Config config, Logger logger) {
        this.splitsToSend = Collections.synchronizedList(splitsToSend);
        this.config = config;
        this.logger = logger;
        this.messageHandlers = new ArrayList<>();
        this.responses = new HashMap<String, ArrayList<String>>();
    }

    public HashMap<String, ArrayList<String>> send() {
        logger.log("preparing Map messages ", true);

        for (int i = 0; i < config.slaves.size(); i++)
            messages.add(new ArrayList<>());

        for (int i = 0; !splitsToSend.isEmpty(); i++) {
            MappingMessage mm = new MappingMessage(getId(messageSequenceCounter), splitsToSend.get(0),
                    config.slaves.get(i % config.slaves.size()));
            splitsToSend.remove(0);
            messages.get(i % config.slaves.size()).add(mm);
            messageSequenceCounter++;
        }

        logger.log("sending map messages, " + config.maxMessageBufferSlave + " messages per slave", true);

        for (int i = 0; i < config.slaves.size(); i++) {
            messageHandlers.add(new MessageHandler<MappingMessage>(messages.get(i), config.maxMessageBufferSlave,
                    config.slaves.get(i).getPort(), logger));
            messageHandlers.get(i).start();
        }
        for (MessageHandler<MappingMessage> messageHandler : messageHandlers) {
            try {
                messageHandler.join();

                logger.log("mapping messages received", true);
                for (Message mrm : messageHandler.getMessages())
                    responses.put(((MappingResponseMessage) mrm).id, ((MappingResponseMessage) mrm).content);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

        return responses;

    }

    private String getId(Integer id) {
        return "#OM_ID::" + id;
    }

}
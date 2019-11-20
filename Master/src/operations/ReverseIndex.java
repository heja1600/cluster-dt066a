package operations;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import logs.Logger;
import network.MessageHandler;
import shared.config.Config;
import shared.message.Message;
import shared.message.ReverseMessage;
import shared.message.ReverseResponseMessage;

public class ReverseIndex {

    private Logger logger;
    private Config config;

    private Integer messageSequenceCounter = 0;
    private HashMap<String, ArrayList<String>> reversed;

    private ArrayList<MessageHandler<ReverseMessage>> messageHandlers;

    private ArrayList<ArrayList<ReverseMessage>> messages = new ArrayList<>();

    private HashMap<String, ArrayList<String>> mappedValues;

    public ReverseIndex(HashMap<String, ArrayList<String>> mappedValues, Logger logger, Config config) {
        this.logger = logger;
        this.mappedValues = mappedValues;
        this.config = config;
        this.reversed = new HashMap<String, ArrayList<String>>(mappedValues.size());
        this.messageHandlers = new ArrayList<>();
    }

    public HashMap<String, ArrayList<String>> send() {
        logger.log("preparing Reverse messages ", true);
        for (int i = 0; i < config.slaves.size(); i++)
            messages.add(new ArrayList<>());

        Integer k = 0;
        for (Map.Entry<String, ArrayList<String>> entry : mappedValues.entrySet()) {

            ReverseMessage rm = new ReverseMessage(getId(messageSequenceCounter), entry.getKey(), entry.getValue(),
                    config.slaves.get(k % config.slaves.size()));
            messages.get(k % config.slaves.size()).add(rm);
            k++;
            messageSequenceCounter++;
        }

        logger.log("sending reverse messages, " + config.maxMessageBufferSlave + " messages per slave", true);
        for (int i = 0; i < config.slaves.size(); i++) {
            messageHandlers.add(new MessageHandler<>(messages.get(i), config.maxMessageBufferSlave,
                    config.slaves.get(i).getPort(), logger));
            messageHandlers.get(i).start();
        }
        for (MessageHandler<ReverseMessage> messageHandler : messageHandlers) {
            try {
                messageHandler.join();

                logger.log("reverse messages received", true);
                for (Message rrm : messageHandler.getMessages())
                    addNewReverseResponse(((ReverseResponseMessage) rrm).reverse);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        return reversed;
    }

    private String getId(Integer id) {
        return "#OM_ID::" + id;
    }

    private void addNewReverseResponse(HashMap<String, ArrayList<String>> reverse) {
        for (Map.Entry<String, ArrayList<String>> entry : reverse.entrySet()) {
            if (!reversed.containsKey(entry.getKey()))
                reversed.put(entry.getKey(), entry.getValue());
            else
                reversed.get(entry.getKey()).addAll(entry.getValue());
        }
    }
}
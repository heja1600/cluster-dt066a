package operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logs.Logger;
import network.MessageHandler;

import shared.config.Config;
import shared.message.Message;
import shared.message.ReduceMessage;
import shared.message.ReduceResponseMessage;

public class ReduceRequest {
    private Config config;
    private Logger logger;
    private HashMap<String, ArrayList<String>> reverse;
    private HashMap<String, Integer> result = new HashMap<>();
    private Integer messageSequenceNumber = 0;

    private ArrayList<ArrayList<ReduceMessage>> messages = new ArrayList<>();
    private ArrayList<MessageHandler<ReduceMessage>> messageHandlers;

    public ReduceRequest(Config config, Logger logger, HashMap<String, ArrayList<String>> reverse) {
        this.config = config;
        this.logger = logger;
        this.reverse = reverse;
        this.messageHandlers = new ArrayList<>();
    }

    public HashMap<String, Integer> send() {

        logger.log("preparing Reduce messages ", true);
        ArrayList<String> keys = new ArrayList<>();

        for (String key : reverse.keySet())
            keys.add(key);

        for (int i = 0; i < config.slaves.size(); i++)
            messages.add(new ArrayList<>());

        HashMap<String, ArrayList<String>> sendingData = new HashMap<>();

        for (int i = 0, bufferSize = 0, piCounter = 0; i < keys.size(); i++) {
            ArrayList<String> reduce = reverse.get(keys.get(i));
            sendingData.put(keys.get(i), reduce);
            bufferSize += reduce.size();
            if (bufferSize > config.maxAmountOfReduceSize || i == keys.size() - 1) {
                ReduceMessage reduceMessage = new ReduceMessage(getId(messageSequenceNumber), sendingData,
                        config.slaves.get(piCounter % config.slaves.size()));
                messages.get(piCounter % config.slaves.size()).add(reduceMessage);
                messageSequenceNumber++;
                sendingData = new HashMap<>();
                bufferSize = 0;
                piCounter++;
            }
        }

        logger.log("sending reduce messages", true);

        for (int i = 0; i < config.slaves.size(); i++) {
            if (messages.get(i).isEmpty())
                continue;
            messageHandlers.add(new MessageHandler<ReduceMessage>(messages.get(i), config.messageWindow,
                    config.slaves.get(i).getPort(), logger));

            messageHandlers.get(i).start();
        }

        for (MessageHandler<ReduceMessage> messageHandler : messageHandlers) {
            try {
                messageHandler.join();

                logger.log("reduce messages received", true);
                for (Message mrm : messageHandler.getMessages())
                    insert(((ReduceResponseMessage) mrm).result);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        return result;
    }

    private String getId(Integer id) {
        return "#RM_ID::" + id;
    }

    private void insert(HashMap<String, Integer> reducedValues) {
        for (Map.Entry<String, Integer> entry : reducedValues.entrySet()) {
            if (!result.containsKey(entry.getKey()))
                result.put(entry.getKey(), entry.getValue());
            else
                result.put(entry.getKey(), result.get(entry.getKey()) + entry.getValue());
        }
    }
}
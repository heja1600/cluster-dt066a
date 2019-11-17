package src.operations;

import shared.message.ReduceMessage;

public class Reducing {
    ReduceMessage reduceMessage;

    public Reducing(ReduceMessage reduceMessage) {
        this.reduceMessage = reduceMessage;
    }

    public Integer reduce() {
        return reduceMessage.words.size(); // this is stupid
    }
}
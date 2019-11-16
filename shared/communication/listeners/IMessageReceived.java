package shared.communication.listeners;

import shared.message.Message;

public interface IMessageReceived {
    public void onMessageReceived(Message message);
}
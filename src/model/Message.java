package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private String message,person;
    private ArrayList<String> clients;
    private MessageType messageType;

    public Message() {
        this.messageType = MessageType.REQUEST_CLIENT_LIST;
    }

    public Message(String name, String password) {
        this.messageType = MessageType.CLIENT_PRIVATE_MESSAGE;
        this.message = name;
        this.person = password;
    }

    public Message(ArrayList<String> clients) {
        this.messageType = MessageType.SEND_CLIENT_LIST;
        this.clients = clients;
    }

    public Message(String message, MessageType messageType) {
        this.messageType = messageType;
        this.message = message;
    }

    public Message(String message, String person, MessageType messageType) {
        this.messageType = messageType;
        this.message = message;
        this.person = person;
    }

    public String getMessage() {
        return message;
    }

    public String getPerson() {
        return person;
    }

    public ArrayList<String> getClients() {
        return clients;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}

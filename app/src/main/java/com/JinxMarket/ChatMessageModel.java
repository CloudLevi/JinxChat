package com.JinxMarket;

public class ChatMessageModel {

    private String sender;
    private String receiver;
    private String message;
    private boolean isRead;

    public ChatMessageModel(){
    }

    public ChatMessageModel(String sender, String receiver, String message, boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isRead = isRead;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }
}

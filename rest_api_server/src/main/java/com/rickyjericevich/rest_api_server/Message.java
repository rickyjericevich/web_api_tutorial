package com.rickyjericevich.rest_api_server;

import java.util.Date;

public class Message {

    private String username;
    private String message;
    private Date timestamp;

    public Message(String username, String message) {
        this.username = username;
        this.message = message;
        this.timestamp = new Date();
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
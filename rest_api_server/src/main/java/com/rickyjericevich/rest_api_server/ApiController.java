package com.rickyjericevich.rest_api_server;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.ArrayList;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("messages")
public class ApiController {

    private ArrayList<Message> messages;

    public ApiController() {
        messages = new ArrayList<Message>();
    }

    @GetMapping("test")
    public ArrayList<Message> getTestMessages() {
        System.out.println("Getting test messages");

        ArrayList<Message> testMessages = new ArrayList<Message>();
        testMessages.add(new Message("test_username_1", "Hello, world!"));
        testMessages.add(new Message("Test Username 2 😎", "This is a test message 🤭"));

        return testMessages;
    }

    @GetMapping()
    public ArrayList<Message> getMessages() {
        System.out.println("Getting all " + messages.size() + " messages");
        return messages;
    }

    @PostMapping()
    public Message newMessage(@RequestBody Message message) {
        System.out.println("Received new message: " + message.toString());
        messages.add(message);
        System.out.println("New message created. Number of messages: " + messages.size());
        return message;
    }

//    @DeleteMapping("/{messageId}")
//    public Message deleteMessage(@PathVariable String messageId) {}

//    @PatchMapping("/{messageId}")
//    public Message editMessage(@PathVariable String messageId) {}
}

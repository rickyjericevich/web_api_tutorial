package com.rickyjericevich.rest_api_server;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("messages")
public class ApiController {

    public ApiController() {}

    // @GetMapping("test")
    // public ArrayList<Message> getTestMessages() {
    //     System.out.println("Getting test messages");

    //     ArrayList<Message> testMessages = new ArrayList<Message>();
    //     testMessages.add(new Message("test_username_1", "Hello, world!"));
    //     testMessages.add(new Message("Test Username 2 😎", "This is a test message 🤭"));

    //     return testMessages;
    // }
}

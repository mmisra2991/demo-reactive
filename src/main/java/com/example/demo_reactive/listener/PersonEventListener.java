package com.example.demo_reactive.listener;

import com.example.demo_reactive.events.PersonEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PersonEventListener {

    @EventListener
    public void handlePersonEvent(PersonEvent event) {
        System.out.println("Received event: " + event.getMessage());
    }
}
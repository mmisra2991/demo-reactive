package com.example.demo_reactive.events;

import com.example.demo_reactive.controller.PersonController;

public record PersonEvent(Object object, String name) {
    public String getMessage() {
        return "Event from PersonController: " + name;
    }
}

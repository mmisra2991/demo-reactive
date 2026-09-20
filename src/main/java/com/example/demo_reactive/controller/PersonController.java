package com.example.demo_reactive.controller;

import com.example.demo_reactive.dto.Person;
import com.example.demo_reactive.dto.User;
import com.example.demo_reactive.events.PersonEvent;
import com.example.demo_reactive.service.PersonService;
import org.jspecify.annotations.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping(value = "/persons", produces = "text/event-stream")
    public Flux<Person> getPersons() {
        this.eventPublisher.publishEvent(new PersonEvent(this, "getPersons called"));
        return personService.getAllPersons();
    }

    @GetMapping(value = "/persons/{id}")
    public Mono<Person> getPerson(@PathVariable("id") @NonNull Integer id) {
        return personService.getPerson(id);
    }

    @PutMapping(value = "/persons/{id}")
    public Flux<Person> updatePerson(@RequestBody @NonNull Person person, @NonNull @PathVariable("id") Integer id){
        return personService.updatePerson(person, id);
    }

    @DeleteMapping(value = "/persons/{id}")
    public Flux<Person> deletePerson(@PathVariable("id") @NonNull Integer id){
        return personService.deletePerson(id);
    }

    @GetMapping("/fetch-users")
    public Flux<User> fetchUsers() {
        return personService.fetchUsers();
    }

}

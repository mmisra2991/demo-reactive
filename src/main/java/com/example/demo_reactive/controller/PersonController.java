package com.example.demo_reactive.controller;

import com.example.demo_reactive.dto.Person;
import com.example.demo_reactive.dto.User;
import com.example.demo_reactive.events.PersonEvent;
import com.example.demo_reactive.service.PersonService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class PersonController {

    private final PersonService personService;
    private final ApplicationEventPublisher eventPublisher;
    private final WebClient webClient;

    public PersonController(PersonService personService, ApplicationEventPublisher eventPublisher, WebClient webClient
                            ) {
        this.personService = personService;
        this.eventPublisher = eventPublisher;
        this.webClient = webClient;
    }

    @GetMapping(value = "/persons", produces = "text/event-stream")
    public Flux<Person> getPersons() {
        this.eventPublisher.publishEvent(new PersonEvent(this, "getPersons called"));
        return personService.getAllPersons();
    }

    @GetMapping(value = "/persons/{id}")
    public Mono<Person> getPerson(@PathVariable("id") Integer id) {
        return personService.getPerson(id);
    }

    @PutMapping(value = "/persons/{id}")
    public Flux<Person> updatePerson(@RequestBody Person person, @PathVariable("id") Integer id){
        return personService.updatePerson(person, id);
    }

    @DeleteMapping(value = "/persons/{id}")
    public Flux<Person> deletePerson(@PathVariable("id") Integer id){
        return personService.deletePerson(id);
    }

    @GetMapping("/fetch-users")
    public Flux<User> fetchUsers() {
        return webClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/users")
                .retrieve()
                .bodyToFlux(User.class)
                // Process up to 5 users concurrently on the boundedElastic scheduler
                .flatMap(user -> processUserConcurrently(user), 5);
    }

    private Flux<User> processUserConcurrently(User user) {
        return Flux.just(user)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(u -> {
                    // Simulate blocking/slow operation per user
                    IO.println(Thread.currentThread().getName() + " processing user: " + u.id());
                });
    }
}

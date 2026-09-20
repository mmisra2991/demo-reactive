package com.example.demo_reactive.service;

import com.example.demo_reactive.dto.Person;
import com.example.demo_reactive.dto.User;
import org.jspecify.annotations.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final WebClient webClient;

    static Flux<Person> persons;
    static {
        persons = Flux.just(
                new Person(1, "Alice"),
                new Person(2, "Bob"),
                new Person(3, "Charlie"),
                new Person(4, "David"),
                new Person(5, "Eve")
        );
    }

    public Flux<Person> getAllPersons() {
        BeanUtils.copyProperties(persons, Person.class);
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        return Flux.zip(persons, interval, (person, tick) -> person);
    }

    public Mono<Person> getPerson(Integer id) {
       return persons
               .filter(p -> p.id().equals(id))
               .doOnNext(person -> System.out.println("Found person: " + person))
               .next();
    }

    public Flux<Person> updatePerson(@NonNull Person updatedPerson,@NonNull Integer id) {
        return persons.map(existingPerson -> {
            if (existingPerson.id() == id) {
                // Return a new instance with updated properties
                IO.println("Updating person with id: " + id + " to new name: " + updatedPerson.name());
                return new Person(id, updatedPerson.name());
            }
            return existingPerson; // Leave other persons unchanged
        });
    }

    public Flux<Person> deletePerson(Integer id) {
        return persons.filter(person -> !person.id().equals(id));
    }

    public Flux<User> fetchUsers(){
        return webClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/users")
                .retrieve()
                .bodyToFlux(User.class)
                // Process up to 5 users concurrently on the boundedElastic scheduler
                .flatMap(this::processUserConcurrently, 5);
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

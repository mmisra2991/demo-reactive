package com.example.demo_reactive.service;

import com.example.demo_reactive.dto.Person;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PersonService {

    Flux<Person> persons = Flux.just(
            new Person(1, "Alice"),
            new Person(2, "Bob"),
            new Person(3, "Charlie"),
            new Person(4, "David"),
            new Person(5, "Eve")
    );

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

    public Flux<Person> updatePerson(Person updatedPerson, Integer id) {
        return persons.map(existingPerson -> {
            if (existingPerson.id() == id) {
                // Return a new instance with updated properties
                return new Person(id, updatedPerson.name());
            }
            return existingPerson; // Leave other persons unchanged
        });
    }

    public Flux<Person> deletePerson(Integer id) {
        return persons.filter(person -> !person.id().equals(id));
    }
}

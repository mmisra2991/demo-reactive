package com.example.demo_reactive.config;

import com.example.demo_reactive.dto.Person;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    Map<String, Person> cache = new HashMap<>();

    @PostConstruct
    public void init(){
        cache.put("Alice", new Person(1, "Alice"));
    }

    public void putCache(Person person){
        if(cache.containsKey(person.name())){
            cache.getOrDefault(person.id(), person);
        }
        cache.put(person.name(), person);
    }

    public Person getCache(String id){
        return cache.get(id);
    }

}

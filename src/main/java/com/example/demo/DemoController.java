package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoRepository demoRepository;

    private DemoController(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @PostMapping
    private ResponseEntity<Void> createDemo(@RequestBody Demo demo, UriComponentsBuilder ucb) {
        Demo savedDemo = demoRepository.save(demo);
        URI locationOfNewDemo = ucb
                .path("demo/{id}")
                .buildAndExpand(savedDemo.id())
                .toUri();
        return ResponseEntity.created(locationOfNewDemo).build();
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Demo> readDemo(@PathVariable Long requestedId) {
        Optional<Demo> demoOptional = demoRepository.findById(requestedId);
        return demoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

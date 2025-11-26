package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Sort;

import java.net.URI;
import java.util.*;

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

    @GetMapping
    private ResponseEntity<List<Demo>> findAll(Pageable pageable) {
        Page<Demo> page = demoRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }
}

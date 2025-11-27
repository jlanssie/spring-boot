package com.example.demo;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
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
    private ResponseEntity<Demo> readDemo(@PathVariable Long requestedId, Principal principal) {
        Demo cashCard = demoRepository.findByIdAndOwner(requestedId, principal.getName());
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    private ResponseEntity<List<Demo>> readDemos(Pageable pageable, Principal principal) {
        Page<Demo> page = demoRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateDemo(@PathVariable Long requestedId, @RequestBody Demo cashCardUpdate, Principal principal) {
        Demo cashCard = demoRepository.findByIdAndOwner(requestedId, principal.getName());
        if (cashCard != null) {
            Demo updatedDemo = new Demo(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            demoRepository.save(updatedDemo);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

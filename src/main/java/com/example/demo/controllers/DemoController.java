package com.example.demo.controllers;

import com.example.demo.pojos.Demo;
import com.example.demo.repositories.DemoRepository;
import com.example.demo.services.ConfigurationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoRepository demoRepository;

    private final ConfigurationService configurationService;

    private DemoController(DemoRepository demoRepository, ConfigurationService configurationService) {
        this.demoRepository = demoRepository;
        this.configurationService = configurationService;
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
        Demo cashCard = findDemo(requestedId, principal);
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
                        pageable.getSort()
                        // pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));

        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateDemo(@PathVariable Long requestedId, @RequestBody Demo cashCardUpdate, Principal principal) {
        Demo cashCard = findDemo(requestedId, principal);
        if (cashCard != null) {
            Demo updatedDemo = new Demo(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            demoRepository.save(updatedDemo);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/config")
    private ResponseEntity<String> readConfig() {
        String message = configurationService.getEnvironmentMessage();
        return ResponseEntity.ok("Env: " + message);
    }

    private Demo findDemo(Long requestedId, Principal principal) {
        return demoRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (demoRepository.existsByIdAndOwner(id, principal.getName())) {
            demoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}

package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoRepository demoRepository;

    private DemoController(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Demo> findById(@PathVariable Long requestedId) {
        Optional<Demo> demoOptional = demoRepository.findById(requestedId);
        return demoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

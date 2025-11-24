package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/{requestedId}")
    private ResponseEntity<Demo> findById(@PathVariable Long requestedId) {
        if (requestedId.equals(99L)) {
            Demo demo = new Demo(99L, 123.45);
            return ResponseEntity.ok(demo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

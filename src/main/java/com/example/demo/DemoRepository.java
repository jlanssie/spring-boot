package com.example.demo;

import org.springframework.data.repository.CrudRepository;

interface DemoRepository extends CrudRepository<Demo, Long> {
}

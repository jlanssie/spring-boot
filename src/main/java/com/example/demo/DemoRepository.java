package com.example.demo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface DemoRepository extends CrudRepository<Demo, Long>, PagingAndSortingRepository<Demo, Long> {
}

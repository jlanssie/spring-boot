package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface DemoRepository extends CrudRepository<Demo, Long>, PagingAndSortingRepository<Demo, Long> {
    boolean existsByIdAndOwner(Long id, String owner);

    Demo findByIdAndOwner(Long id, String owner);

    Page<Demo> findByOwner(String owner, PageRequest pageRequest);
}

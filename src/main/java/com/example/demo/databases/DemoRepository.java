package com.example.demo.databases;

import com.example.demo.data.Demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DemoRepository extends CrudRepository<Demo, Long>, PagingAndSortingRepository<Demo, Long> {
    boolean existsByIdAndOwner(Long id, String owner);

    Demo findByIdAndOwner(Long id, String owner);

    Page<Demo> findByOwner(String owner, Pageable pageable);
}

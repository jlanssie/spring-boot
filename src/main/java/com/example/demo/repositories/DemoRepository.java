package com.example.demo.repositories;

import com.example.demo.pojos.Demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DemoRepository extends JpaRepository<Demo, Long>, PagingAndSortingRepository<Demo, Long> {
    boolean existsByIdAndOwner(Long id, String owner);

    Demo findByIdAndOwner(Long id, String owner);

    Page<Demo> findByOwner(String owner, Pageable pageable);
}

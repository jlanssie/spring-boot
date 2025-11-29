package com.example.demo.repositories;

import com.example.demo.pojos.Demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface DemoRepository extends CrudRepository<Demo, Long>{
    boolean existsByIdAndOwner(Long id, String owner);

    Demo findByIdAndOwner(Long id, String owner);

    Page<Demo> findByOwner(String owner, Pageable pageable);
}

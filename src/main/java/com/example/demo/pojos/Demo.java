package com.example.demo.pojos;

import org.springframework.data.annotation.Id;

public record Demo(@Id Long id, Double amount, String owner)  {
}
package com.example.demo.data;

import org.springframework.data.annotation.Id;

public record Demo(@Id Long id, Double amount, String owner)  {
}
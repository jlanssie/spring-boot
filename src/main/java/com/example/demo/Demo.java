package com.example.demo;

import org.springframework.data.annotation.Id;

record Demo(@Id Long id, Double amount, String owner)  {
}
package com.example.todo.web;

import jakarta.validation.constraints.NotBlank;

public class TodoDtos {
    public static record TodoRequest(@NotBlank String title, String description, boolean completed) {}
}

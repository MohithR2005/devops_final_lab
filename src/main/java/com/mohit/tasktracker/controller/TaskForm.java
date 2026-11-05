package com.mohit.tasktracker.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskForm {
    @NotBlank(message = "Please enter a task title.")
    @Size(max = 120, message = "Use no more than 120 characters.")
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

package com.mohit.tasktracker.model;

/** Immutable task data; updates replace the record rather than mutate it. */
public record Task(long id, String title, boolean completed) {}

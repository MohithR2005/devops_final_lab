package com.mohit.tasktracker.service;

import com.mohit.tasktracker.model.Task;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class TaskService {
    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private long nextId = 1;

    // Synchronization keeps operations safe when multiple HTTP requests arrive.
    public synchronized List<Task> findAll() {
        return List.copyOf(tasks.values());
    }

    public synchronized Task add(String title) {
        if (title == null || title.isBlank() || title.length() > 120) {
            throw new IllegalArgumentException("Enter a task between 1 and 120 characters.");
        }
        Task task = new Task(nextId++, title.strip(), false);
        tasks.put(task.id(), task);
        return task;
    }

    public synchronized void complete(long id) {
        Task task = tasks.get(id);
        if (task == null) throw new NoSuchElementException("Task not found");
        tasks.put(id, new Task(id, task.title(), true));
    }

    public synchronized void delete(long id) {
        if (tasks.remove(id) == null) throw new NoSuchElementException("Task not found");
    }
}

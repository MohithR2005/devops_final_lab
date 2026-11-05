package com.mohit.tasktracker.service;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    @Test void addsTrimmedTaskAndCompletesIt() {
        var service = new TaskService();
        var task = service.add("  Learn Maven  ");
        assertEquals("Learn Maven", task.title());
        assertFalse(task.completed());
        service.complete(task.id());
        service.complete(task.id()); // Completing twice should remain completed.
        assertTrue(service.findAll().getFirst().completed());
    }

    @Test void rejectsInvalidTitles() {
        var service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.add("   "));
        assertThrows(IllegalArgumentException.class, () -> service.add(null));
        assertThrows(IllegalArgumentException.class, () -> service.add("x".repeat(121)));
        assertTrue(service.findAll().isEmpty());
    }

    @Test void deletesOnlyRequestedTaskAndDoesNotReuseIds() {
        var service = new TaskService();
        var first = service.add("First");
        var second = service.add("Second");
        service.delete(first.id());
        assertEquals(java.util.List.of(second), service.findAll());
        assertTrue(service.add("Third").id() > second.id());
    }

    @Test void missingTasksAreReported() {
        var service = new TaskService();
        assertThrows(NoSuchElementException.class, () -> service.complete(99));
        assertThrows(NoSuchElementException.class, () -> service.delete(99));
    }
}

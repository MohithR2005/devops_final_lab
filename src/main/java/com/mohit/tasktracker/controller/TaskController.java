package com.mohit.tasktracker.controller;

import com.mohit.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@Controller
public class TaskController {
    private final TaskService service;
    private final String version;

    public TaskController(TaskService service, @Value("${app.version:1.0.0}") String version) {
        this.service = service;
        this.version = version;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("taskForm", new TaskForm());
        return renderTasks(model);
    }

    @PostMapping("/tasks")
    public String add(@Valid @ModelAttribute("taskForm") TaskForm form,
                      BindingResult result, Model model) {
        if (result.hasErrors()) return renderTasks(model);
        service.add(form.getTitle());
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/complete")
    public String complete(@PathVariable long id) {
        service.complete(id);
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable long id) {
        service.delete(id);
        return "redirect:/";
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String notFound() { return "Task not found. Return to the home page and refresh."; }

    private String renderTasks(Model model) {
        var tasks = service.findAll();
        model.addAttribute("tasks", tasks);
        model.addAttribute("completedCount", tasks.stream().filter(t -> t.completed()).count());
        model.addAttribute("version", version);
        return "index";
    }
}

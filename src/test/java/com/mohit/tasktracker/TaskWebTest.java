package com.mohit.tasktracker;

import com.mohit.tasktracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskWebTest {
    @Value("${local.server.port}") int port;
    @Value("${app.version}") String version;
    @Autowired TaskService service;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach void clearTasks() {
        service.findAll().forEach(task -> service.delete(task.id()));
    }

    @Test void rendersHomeAndHealth() throws Exception {
        var home = get("/");
        assertEquals(200, home.statusCode());
        assertTrue(home.body().contains("Your tasks"));
        assertTrue(home.body().contains("v" + version));
        assertEquals(200, get("/style.css").statusCode());
        var health = get("/actuator/health");
        assertEquals(200, health.statusCode());
        assertTrue(health.body().contains("UP"));
    }

    @Test void createsCompletesAndDeletesThroughHttp() throws Exception {
        assertEquals(302, post("/tasks", "title=Learn+Docker").statusCode());
        assertTrue(get("/").body().contains("Learn Docker"));
        long id = service.findAll().getFirst().id();
        assertEquals(302, post("/tasks/" + id + "/complete", "").statusCode());
        assertTrue(get("/").body().contains("1 / 1 complete"));
        assertTrue(service.findAll().getFirst().completed());
        assertEquals(302, post("/tasks/" + id + "/delete", "").statusCode());
        assertTrue(service.findAll().isEmpty());
    }

    @Test void validatesInputAndEscapesUserHtml() throws Exception {
        var invalid = post("/tasks", "title=+++");
        assertEquals(200, invalid.statusCode());
        assertTrue(invalid.body().contains("Please enter a task title."));
        assertTrue(service.findAll().isEmpty());
        var tooLong = post("/tasks", "title=" + "x".repeat(121));
        assertTrue(tooLong.body().contains("Use no more than 120 characters."));
        assertTrue(service.findAll().isEmpty());
        post("/tasks", "title=%3Cscript%3Ealert%281%29%3C%2Fscript%3E");
        String html = get("/").body();
        assertFalse(html.contains("<script>alert(1)</script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test void missingTaskReturns404() throws Exception {
        assertEquals(404, post("/tasks/99999/complete", "").statusCode());
        assertEquals(404, post("/tasks/99999/delete", "").statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
}

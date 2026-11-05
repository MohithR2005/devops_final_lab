# Task Tracker

A Java and Thymeleaf web application for DevOps CIA 2. This is the local application stage; Docker, CI/CD and cloud deployment will be added in later learning steps.

## Run locally

Open this folder (the one containing pom.xml) in VS Code. Use JDK 21 or a compatible newer JDK. Maven is supplied through the official Maven Wrapper; the first build needs internet access.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-24'
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

On this machine JAVA_HOME was pointing to JDK 17 even though `java -version` reported 24. The first line selects the installed JDK 24 for this terminal only. Run `.\mvnw.cmd -version` to check the Java version Maven actually uses. Other machines should set JAVA_HOME to their own compatible JDK location.

Open http://localhost:8080. Stop the application with Ctrl+C. On Linux use `./mvnw` instead of `.\mvnw.cmd`.

## Understand the request flow

1. A browser sends GET / to TaskController.
2. The controller asks TaskService for the current tasks and puts them in the model.
3. Thymeleaf renders templates/index.html as HTML for the browser.
4. A form submits a POST request to add, complete or delete a task.
5. The controller validates input and calls the service, then redirects to GET / to avoid resubmission on refresh.

## Files to learn

| File | Responsibility |
| --- | --- |
| pom.xml | Dependencies, Java target version and Maven build plugin |
| TasktrackerApplication.java | Starts Spring Boot and component scanning |
| model/Task.java | Immutable task ID, title and completion state |
| service/TaskService.java | Task operations and in-memory storage |
| controller/TaskForm.java | Form data with validation constraints |
| controller/TaskController.java | HTTP routes and model preparation |
| templates/index.html | Server-rendered website using Thymeleaf |
| static/style.css | Responsive visual styling |
| application.properties | App version, port and health endpoint settings |

Java source files are under src/main/java/com/mohit/tasktracker. Templates, CSS and configuration are under src/main/resources.

## Tests and health

TaskServiceTest tests validation and task lifecycle behavior. TaskWebTest starts a real HTTP server on a random port and checks rendering, form submission, validation, HTML escaping, missing tasks and health. Maven reports are in target/surefire-reports after running verify.

Health endpoint: /actuator/health. Kubernetes probes can later use /actuator/health/liveness and /actuator/health/readiness.

## Scope and limitations

Tasks are shared by all visitors and stored only in process memory. Restarting or redeploying clears them; multiple replicas would have separate task lists. This first version has no authentication or CSRF protection and is for an assignment demo, not private or sensitive data. We must revisit persistence, replicas and access protection before treating it as a production service.

Change app.version in application.properties for a visible redeployment demonstration later.

## Viva prompts

- What is the difference between a controller, service and model?
- How do @SpringBootApplication, @Controller and @Service affect object creation?
- Why do modifying routes use POST and redirect afterwards?
- How does @Valid trigger validation, and what does BindingResult contain?
- Why must server-side validation exist even when HTML has required and maxlength?
- What do Maven clean, test, package and verify do?
- Why is in-memory storage unsuitable for multiple replicas?
- How do unit tests differ from the HTTP integration tests here?

# Task Tracker

A Java and Thymeleaf web application for DevOps CIA 2, with Maven tests, Docker packaging, ECR image publishing and automated K3s deployment on AWS EC2.

## Submission and live demo

- Live application: http://13.236.232.232 (EC2 must be running; its public IP can change after stop/start).
- Pipeline: [GitHub Actions](https://github.com/MohithR2005/devops_final_lab/actions).
- Start here: [Demo and viva guide](docs/DEMO-AND-VIVA.md).
- Kubernetes configuration: [k8s](k8s/).
- Configuration management: [Ansible playbook](ansible/deploy.yml) and [inventory](ansible/inventory.ini).

## Automated delivery

Push application/configuration changes to main. GitHub Actions runs `clean verify` on a hosted Java 21 runner and uploads the tested JAR and test reports. The dependent deployment job runs on the dedicated EC2 runner, builds `Dockerfile.runtime` using only that JAR, publishes a uniquely tagged image to ECR, then runs Ansible to deploy and verify it.

The original multi-stage `Dockerfile` remains available for a complete local build from source. CI uses `Dockerfile.runtime` to avoid compiling twice and to keep the small cloud server's workload low.

An EC2 instance role provides temporary AWS credentials. Its publishing policy is restricted to the final-lab ECR repository. GitHub OIDC provider creation was blocked by the account's organization policy, so this deployment uses a self-hosted runner. No static AWS keys or SSH private keys are stored in GitHub.

Only main-branch push and manual runs deploy. Documentation-only changes do not trigger deployment. Do not add pull-request triggers to the privileged runner workflow or grant untrusted collaborators write access. The runner has Docker access and passwordless sudo, approved for this assignment server; it is not a production isolation model.

Ansible installs a systemd timer to renew the ECR image-pull Secret every six hours and also renews it before deployment. Kubernetes manifests contain no credentials. The Secret is generated on the server from temporary ECR credentials.

## Run locally

Open this folder (the one containing pom.xml) in VS Code. Use JDK 21 or a compatible newer JDK. Maven is supplied through the official Maven Wrapper; the first build needs internet access.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-24'
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

On this machine JAVA_HOME was pointing to JDK 17 even though `java -version` reported 24. The first line selects the installed JDK 24 for this terminal only. Run `.\mvnw.cmd -version` to check the Java version Maven actually uses. Other machines should set JAVA_HOME to their own compatible JDK location.

Open http://localhost:8080. Stop the application with Ctrl+C. On Linux use `./mvnw` instead of `.\mvnw.cmd`.

## Run with Docker

Start Docker Desktop using Linux containers, then run these commands from this folder:

```powershell
docker build -t final-lab:1.0.0 .
docker run -d --name final-lab -p 127.0.0.1:8081:8080 final-lab:1.0.0
```

Open http://localhost:8081. The host port is 8081 so the original local Java application can keep using 8080. The application inside the container listens on 8080. The local demo is bound to the host loopback interface.

The build stage uses Java 21 and the Maven Wrapper to compile, run all tests and package the JAR. A failing test stops the image build. The runtime stage contains a Java 21 JRE and the JAR, runs as a non-root user, and does not include Maven or source code. This follows Docker's multi-stage approach: https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/

The .dockerignore file limits the build context to required inputs. The Dockerfile builds from source instead of copying a potentially stale JAR from the Windows target directory.

```powershell
docker logs final-lab
curl.exe http://localhost:8081/actuator/health
docker stop final-lab
docker start final-lab
```

Stopping and restarting clears the in-memory tasks. To replace the container after rebuilding, stop it and run `docker rm final-lab`, then repeat the run command. Images are packaged templates; containers are running instances. `EXPOSE` documents the container port; `-p` publishes it on the host.

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

Health endpoint: /actuator/health. Kubernetes probes use /actuator/health/liveness and /actuator/health/readiness.

## Scope and limitations

Tasks are shared by all visitors and stored only in process memory. Restarting or redeploying clears them; multiple replicas would have separate task lists. This first version has no authentication or CSRF protection and is for an assignment demo, not private or sensitive data. We must revisit persistence, replicas and access protection before treating it as a production service.

Change app.version in application.properties and push to main for a visible redeployment demonstration. The HTTP test checks the configured version dynamically.

## Viva prompts

- What is the difference between a controller, service and model?
- How do @SpringBootApplication, @Controller and @Service affect object creation?
- Why do modifying routes use POST and redirect afterwards?
- How does @Valid trigger validation, and what does BindingResult contain?
- Why must server-side validation exist even when HTML has required and maxlength?
- What do Maven clean, test, package and verify do?
- Why is in-memory storage unsuitable for multiple replicas?
- How do unit tests differ from the HTTP integration tests here?

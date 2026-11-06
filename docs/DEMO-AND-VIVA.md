# Demo and viva guide

## Your one minute introduction

“I built a Java Spring Boot task tracker with a Thymeleaf website. Maven compiles it and runs automated tests. A push to main starts GitHub Actions. The first job builds and tests on a GitHub-hosted runner and saves the tested JAR. The second job runs on my EC2 deployment runner, packages that JAR into a Docker image, pushes it to Amazon ECR, and uses Ansible to deploy it to K3s Kubernetes. Kubernetes health checks and an HTTP check verify the release. Traefik routes browser traffic to the application.”

## Before the presentation

1. Keep the EC2 instance running. The website and runner are services; your laptop and SSH session do not need to remain connected.
2. Open the live website at http://13.239.8.193. Confirm the public IP in EC2 if the instance was stopped and started.
3. Open https://github.com/MohithR2005/devops_final_lab/actions and check the last deployment is green.
4. Open AWS in Sydney (ap-southeast-2), with the EC2 instance and ECR final-lab repository in separate tabs.
5. For terminal evidence, use EC2 Connect > Session Manager if available. Run `sudo kubectl get nodes` and `sudo kubectl get pods,svc,ingress -n final-lab`.
6. If using SSH, obtain your current public IP and put it with /32 in the SSH security group rule. A new hotspot may need a new rule. Viewing the website does not require this.

## Show the project in five minutes

1. Show the website. Add “Explain my CI/CD pipeline”, complete it, and delete it.
2. Show the source repository: pom.xml, Java classes, tests, Dockerfiles, workflow, Kubernetes YAML and Ansible playbook.
3. Show a successful Actions run. Explain the two jobs and the dependency between them. Open Maven results and deployment logs.
4. Show the ECR image tag, then the running Kubernetes pod and its image.
5. Demonstrate a real change using the steps below. Do not manually build or run kubectl to deploy this change.

## Demonstrate an automatic change

Use the GitHub website so you do not need SSH or a local terminal.

1. Open `src/main/resources/application.properties` in the GitHub repository.
2. Click the pencil button to edit. Change only `app.version` to the next value, for example from `1.0.1` to `1.0.2`.
3. Click Commit changes. Use the message `Demo: update visible application version` and commit directly to main.
4. Open Actions. The new run starts automatically. Explain the checkout, Java setup, Maven build/tests and JAR artifact while the first job runs.
5. Open the deploy job. Show Docker build and ECR push, then the Ansible tasks and successful Kubernetes rollout.
6. Wait for both jobs to turn green. Refresh the live website with Ctrl+F5. The version badge must show the new number.
7. Point out that the updated website came from the Git commit without manually logging in and deploying.

Allow several minutes for the run. The first build can take longer because dependencies and base images are downloaded. Updates briefly interrupt the site because this small demo uses a single replica with the Recreate strategy. Tasks reset because storage is in memory.

If you edit on GitHub, run `git pull --ff-only` in your local tasktracker folder before your next local edit/push.

## What we did in order

1. Built the Java model, service, controller, validation and HTML website.
2. Added automated tests and built an executable JAR using Maven Wrapper.
3. Committed the application to Git and pushed it to GitHub.
4. Added a multi-stage Dockerfile and tested the container locally.
5. Created an ECR repository and published the initial image.
6. Launched Ubuntu EC2, configured network access and an IAM role.
7. Installed K3s and deployed a namespace, Deployment, Service and Ingress.
8. Configured GitHub Actions with hosted CI and an EC2 deployment runner.
9. Added Ansible to manage image-pull credentials, their timer and the application deployment.
10. Verified a pushed version change travels through the pipeline and appears in the website.

## Viva questions with short answers

| Question | Answer |
|---|---|
| What is CI? | Automatically building and testing each integrated code change. Here the build-test job runs Maven verify. |
| What is CD here? | Continuous deployment: after CI succeeds, the main-branch release is automatically published and deployed without manual approval. |
| Why Git and GitHub? | Git tracks versions locally. GitHub hosts the repository and runs Actions workflows. |
| What is Spring Boot? | It configures and starts the Java web application, including an embedded web server. |
| Why Thymeleaf? | It renders HTML on the server using model values from the controller. |
| Controller versus service versus model? | Controller handles HTTP requests; service implements task operations; model represents task data. |
| What does Maven do? | Resolves dependencies, compiles source, runs tests and packages the executable JAR. |
| What is pom.xml? | Maven project configuration: dependencies, Java target and build plugins. |
| What is the Maven Wrapper? | Project scripts that obtain the configured Maven version so a global Maven installation is unnecessary. A JDK is still needed. |
| clean, test, package, verify? | clean removes previous output; test runs tests; package creates the JAR; verify runs lifecycle phases through verification, including tests and packaging. |
| Unit versus integration tests? | Service tests check task operations. HTTP tests start a real web server and exercise routes, validation and rendering. |
| Image versus container? | An image packages the application and runtime. A container is a running instance of an image. |
| Why two Dockerfiles? | Dockerfile builds/tests from source for local reproducibility. Dockerfile.runtime packages the exact JAR already tested by hosted CI, avoiding a heavy Maven build on the small EC2 server. |
| What is ECR? | AWS's container image registry. It stores images; it does not run the website. |
| What is EC2? | The Ubuntu virtual machine hosting K3s, the deployment runner and application containers. |
| What is K3s? | A lightweight Kubernetes distribution suitable for this single-node assignment cluster. It is not EKS or ECS. |
| What is a Pod? | Kubernetes's smallest deployable unit. This pod contains the Java application container. |
| What is a Deployment? | It maintains the desired number of application pods and manages replacement during updates. |
| What is a Service? | A stable internal endpoint that selects application pods using labels. Here port 80 forwards to container port 8080. |
| What is an Ingress? | HTTP routing rules. Traefik implements them and forwards external requests to the Service. |
| Why ClusterIP has no external IP? | It is internal by design. Traefik exposes the application through the EC2 public address. |
| Why one replica? | The server is small and tasks are in process memory. Independent replicas would hold different task lists. |
| Why Recreate? | It avoids running old and new Java pods simultaneously on a small server. The tradeoff is brief downtime. |
| What are health probes? | Startup allows time to boot; readiness decides whether traffic should reach the pod; liveness detects when a container should be restarted. |
| Requests versus limits? | Requests guide scheduling; limits cap CPU/memory use. Exceeding memory can cause an OOM kill. |
| Why immutable image tags? | Each commit/run/attempt gets a unique tag, making releases traceable and preventing overwrite conflicts. |
| What is Ansible doing? | It installs the ECR renewal script and systemd timer, renders the image reference, applies Kubernetes YAML and checks rollout/health. |
| What is inventory.ini? | It defines localhost as the managed machine, since the playbook runs directly on EC2. |
| What does become mean? | Ansible escalates privileges using sudo for server configuration and Kubernetes administration. |
| What is idempotency? | Re-running configuration converges to the desired state without unnecessary changes. Some verification commands always run but do not change resources. |
| How does AWS authentication work? | The EC2 instance profile supplies temporary role credentials. No AWS access keys are stored in GitHub. |
| Why refresh the ECR Secret? | ECR login tokens expire after 12 hours. A systemd timer renews it every six hours and deployment also renews it before pulling. |
| Why the self-hosted runner? | The AWS organization policy blocked GitHub OIDC provider creation. A dedicated EC2 runner uses the approved existing instance role. |
| What happens if tests fail? | The build job fails and the dependent deployment job does not run; the currently deployed website remains. |
| What happens if rollout fails? | The deploy job fails and logs show the problem. Automatic rollback is not implemented; use a known good commit and redeploy. |
| Is Docker running the Kubernetes pod? | K3s uses containerd to run the pod. Docker is used to build and push the image. |
| Is this production ready? | No. It is a single-node, HTTP, in-memory assignment application with a privileged deployment runner. Production needs durable storage, TLS, stronger isolation and high availability. |

## Troubleshooting during the demo

- SSH timeout: check current public IP and port 22 rule, or use Session Manager. No need to reinstall K3s.
- Deploy job queued: EC2 must be running and Settings > Actions > Runners should show final-lab-ec2 online.
- Site briefly unavailable during deployment: wait for rollout; Recreate stops the old pod first.
- ImagePullBackOff: inspect pod events and run `sudo systemctl start final-lab-ecr-refresh.service`, then retry rollout.
- Failed build: open the failing step and test reports; fix the error in a new commit.
- Old website version: confirm the latest run is green and perform Ctrl+F5.
- Lost tasks after update: expected because the task list is in memory.

## Read-only terminal evidence

```bash
sudo kubectl get nodes -o wide
sudo kubectl get pods,svc,ingress -n final-lab
sudo kubectl get deployment final-lab -n final-lab -o jsonpath='{.spec.template.spec.containers[0].image}'
sudo kubectl logs deployment/final-lab -n final-lab --tail=30
sudo systemctl list-timers final-lab-ecr-refresh.timer
curl -s http://localhost/actuator/health
```

## After evaluation

Do not terminate the instance until evaluation is finished. Stop it when it is no longer needed to reduce compute charges; EBS and ECR storage can still incur charges. A later start may assign a different public IP. For permanent teardown, remove the runner registration and its server, project IAM permissions, ECR images and unneeded storage after retaining your submission evidence.

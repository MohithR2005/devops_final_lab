# CIA 2 submission

## Submit these

1. Repository: https://github.com/MohithR2005/devops_final_lab
2. [Technical report](TECHNICAL-REPORT.pdf), approximately seven pages.
3. [Evidence folder](evidence/) with screenshots, test results and deployment output.
4. Live website: http://13.239.8.193 (check EC2's current public IP after any stop/start).

Use [DEMO-CHEATSHEET.pdf](DEMO-CHEATSHEET.pdf) during the presentation and [DEMO-AND-VIVA.md](DEMO-AND-VIVA.md) for the fuller viva preparation.

## Requirement mapping

| Assignment requirement | Implementation |
|---|---|
| Java website | Spring Boot MVC, Thymeleaf and task model/service/controller |
| Maven build and tests | pom.xml, Maven Wrapper, nine tests |
| Version control | Public GitHub repository and traceable commits |
| Containerization | Dockerfile, Dockerfile.runtime, .dockerignore |
| Trigger on code push | .github/workflows/ci-cd.yml, main-branch push |
| Container registry | AWS ECR final-lab, unique immutable tags |
| Cloud Kubernetes | K3s on Ubuntu EC2 in Sydney |
| Deployment configuration | Namespace, Deployment, Service and Ingress YAML |
| Ansible | Local inventory and deployment/configuration playbook |
| Verification | Tests, rollout wait, probes, HTTP health and visible-version check |
| Change demonstration | 1.0.0 to 1.0.1 commit and successful automatic deployment |
| Evidence and report | docs/evidence and TECHNICAL-REPORT.pdf |

## Verified demonstration

- Initial automated pipeline: https://github.com/MohithR2005/devops_final_lab/actions/runs/42073698668
- Visible version-change pipeline: https://github.com/MohithR2005/devops_final_lab/actions/runs/42074750277
- Demonstration commit: 563ee2fff28b61a817c10de1611d4b5bf9ba58f5
- Tests in that run: 9 passed, 0 failures, 0 errors.

The report captures this demonstrated release. Later documentation commits may appear above it in GitHub history; documentation-only pushes do not redeploy.

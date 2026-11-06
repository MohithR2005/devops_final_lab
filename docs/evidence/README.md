# Evidence provenance

These files record actual project results; resource screenshots and logs represent the time they were captured.

- `website-before.png`: browser capture of the live application at version 1.0.0 before the demonstrated change.
- `website-after.png`: browser capture after deployment of version 1.0.1, generated on a GitHub-hosted runner.
- `pipeline-success.png`: public GitHub Actions run page for the successful version-change run.
- `public-health.json`: public endpoint response checked from the hosted screenshot runner.
- `ec2-console.png`: user-provided AWS console screenshot from instance/role setup.
- `ecr-initial-console.png`: user-provided AWS console screenshot of the initial 1.0.0 image.
- `cloud-resources.json`: selected live EC2 resource fields retrieved through the AWS API.
- `ecr-images.json`: ECR tags, digests and image sizes retrieved after the version-change deployment.
- `run-42074750277.json`: run and job/step status retrieved through GitHub's REST API.
- `test-results.json`: summary parsed from the run's original uploaded Surefire XML test reports.
- `kubernetes-verification.txt`: actual EC2 command output retrieved using Systems Manager. It includes the ready node and pod, Service, Ingress, deployed image, timer, renewal result and health.

The optional Capture public demo evidence workflow records browser screenshots. It is separate from the two-job Build test and deploy final-lab pipeline and does not deploy the application.

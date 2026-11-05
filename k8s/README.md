# Kubernetes deployment

These files reproduce the manually deployed final-lab application on K3s.

1. Apply namespace.yaml before creating the ECR image-pull Secret.
2. Create or refresh ecr-secret in the final-lab namespace using the EC2 IAM role.
3. Apply final-lab.yaml and verify the rollout.

```bash
sudo kubectl apply -f k8s/namespace.yaml
# Create or refresh ecr-secret before the next command.
sudo kubectl apply -f k8s/final-lab.yaml
sudo kubectl rollout status deployment/final-lab -n final-lab --timeout=300s
sudo kubectl get pods,svc,ingress -n final-lab
```

The deployment uses one replica and Recreate to fit the t3.small server. Updates cause brief downtime and erase in-memory tasks. Traefik exposes the application over HTTP port 80. TLS and persistent storage are not configured.

The checked-in image is the initial tag 1.0.0. The Ansible playbook renders this manifest with the pipeline's unique image tag before applying it. Each tag contains the Git commit, workflow run ID and attempt, making releases traceable and allowing reruns with immutable ECR tags.

ECR credentials expire after 12 hours. Ansible installs final-lab-ecr-refresh.timer to renew them every six hours and refreshes them immediately before each deployment. Do not commit the Secret or its credentials to Git. The application remains single-replica with brief downtime during replacement.

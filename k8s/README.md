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

The image is initially pinned to tag 1.0.0. The future pipeline must render the manifest with the new image reference before applying it, rather than first applying the old tag and then changing it.

ECR credentials expire after 12 hours. Secret renewal is still manual at this stage and must be automated for the completed pipeline. Do not commit the Secret or its credentials to Git.

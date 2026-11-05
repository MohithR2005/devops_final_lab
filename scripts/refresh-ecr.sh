#!/usr/bin/env bash
set -euo pipefail
export PATH=/usr/local/bin:/usr/bin:/bin:$PATH
# The EC2 instance role supplies temporary AWS credentials. Never log this token.
aws ecr get-login-password --region ap-southeast-2 | python3 -c '
import base64, json, sys
password = sys.stdin.read().strip()
if not password:
    raise SystemExit("ECR returned no password")
auth = base64.b64encode(("AWS:" + password).encode()).decode()
config = {"auths": {"656972167295.dkr.ecr.ap-southeast-2.amazonaws.com": {"auth": auth}}}
secret = {"apiVersion": "v1", "kind": "Secret", "metadata": {"name": "ecr-secret", "namespace": "final-lab"}, "type": "kubernetes.io/dockerconfigjson", "data": {".dockerconfigjson": base64.b64encode(json.dumps(config).encode()).decode()}}
print(json.dumps(secret))
' | kubectl apply --server-side --field-manager=final-lab-ecr --force-conflicts -f - >/dev/null

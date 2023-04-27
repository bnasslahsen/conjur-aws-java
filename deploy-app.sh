#!/bin/bash

set -a
source ".env"
set +a

kubectl config set-context --current --namespace=bnl-demo-app-ns

kubectl delete serviceaccount conjur-aws-java-sa --ignore-not-found=true
kubectl create serviceaccount conjur-aws-java-sa

kubectl delete configmap conjur-connect-aws-java --ignore-not-found=true

kubectl create configmap conjur-connect-aws-java \
  --from-literal CONJUR_ACCOUNT="$CONJUR_ACCOUNT" \
  --from-literal CONJUR_APPLIANCE_URL="$CONJUR_APPLIANCE_URL" \
  --from-literal CONJUR_AUTHN_URL="$CONJUR_AUTHN_URL" \
  --from-file "CONJUR_SSL_CERTIFICATE=$CONJUR_CERT_FILE"

# DEPLOYMENT
kubectl replace --force -f deployment.yml
if ! kubectl wait deployment conjur-aws-java --for condition=Available=True --timeout=90s
  then exit 1
fi

kubectl get pods


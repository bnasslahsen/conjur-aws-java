# conjur-aws-java

## Building the Docker images
```shell
mvn clean spring-boot:build-image
```

## Configuring the environment
Set the following environment variables:
```shell    
CONJUR_APPLIANCE_URL=https://ip-10-0-20-225.eu-west-2.compute.internal
# Conjur IAM authn ID - leave as is
CONJUR_ACCOUNT=devsecops
CONJUR_AUTHN_URL="$CONJUR_APPLIANCE_URL"/authn-iam/demo-aws
CONJUR_CERT_FILE=$HOME/conjur-server.pem 
AWS_SERVICE_TYPE="EC2"
```
AWS_SERVICE_TYPE: can be either EC2, EKS or LAMBDA. And can be extended to other AWS services.

## Test on EC2
```shell
java -jar conjur-aws-java-1.0-SNAPSHOT.jar
```

## Deploy to EKS
```shell
./deploy-app.sh
```

## Deploy as LAMBDA function
```shell
mvn clean package -Plambda
```
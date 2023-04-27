FROM eclipse-temurin:17-jre-alpine
COPY target/conjur-aws-java-1.0-SNAPSHOT.jar /app/target/conjur-aws-java-1.0-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/app/target/conjur-aws-java-1.0-SNAPSHOT.jar" ]
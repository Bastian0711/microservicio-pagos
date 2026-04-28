FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/backend-api-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

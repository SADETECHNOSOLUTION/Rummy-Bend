FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8762
ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-jar", "app.jar"]
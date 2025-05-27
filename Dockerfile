FROM openjdk:21
WORKDIR /app
COPY target/javafx-ui-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

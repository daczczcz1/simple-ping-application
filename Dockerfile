FROM eclipse-temurin:17-jdk-alpine

COPY /target/simple-ping-application-1.0-SNAPSHOT-jar-with-dependencies.jar /simple-ping-application.jar

CMD ["java", "-jar", "simple-ping-application.jar"]

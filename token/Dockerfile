FROM openjdk:17-jdk-slim
COPY target/*.jar token.jar
ENTRYPOINT ["java", "-jar", "token.jar"]
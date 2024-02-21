FROM openjdk:17-jdk-slim
COPY target/*.jar authorization.jar
ENTRYPOINT ["java", "-jar", "authorization.jar"]
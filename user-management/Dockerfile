FROM openjdk:17-jdk-slim
COPY target/*.jar user-management.jar
ENTRYPOINT ["java", "-jar", "user-management.jar"]
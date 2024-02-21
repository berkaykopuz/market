FROM openjdk:17-jdk-slim
COPY target/*.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]
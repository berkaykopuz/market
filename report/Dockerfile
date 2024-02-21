FROM openjdk:17-jdk-slim
COPY target/*.jar report.jar
ENTRYPOINT ["java", "-jar", "report.jar"]
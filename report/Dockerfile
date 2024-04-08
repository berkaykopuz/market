FROM openjdk:17-jdk-slim
COPY ./app/images/background.jpg /app/images/background.jpg
COPY target/*.jar report.jar
ENTRYPOINT ["java", "-jar", "report.jar"]
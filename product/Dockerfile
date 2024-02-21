FROM openjdk:17-jdk-slim
COPY target/*.jar product.jar
ENTRYPOINT ["java", "-jar", "product.jar"]
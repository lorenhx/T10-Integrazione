FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/RemoteCCC-0.0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
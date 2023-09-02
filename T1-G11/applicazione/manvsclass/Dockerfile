FROM maven:3.9.4-eclipse-temurin-17

COPY target/manvsclass-0.0.1-SNAPSHOT.jar /app/manvsclass.jar

WORKDIR /app

EXPOSE 8080
CMD ["java","-jar","manvsclass.jar"]
#---------------BUILD STAGE --------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

#------------------RUNTIME STAGE------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/ec2confi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
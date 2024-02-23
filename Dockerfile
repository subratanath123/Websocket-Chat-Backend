FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN ./gradlew build

FROM openjdk:17.0.1-jdk-slim
COPY --from=build build/libs/One-Dollar-Customer-0.0.1-SNAPSHOT.jar One-Dollar-Customer.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","One-Dollar-Customer.jar"]
##### Dockerfile #####
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR ./src
COPY . .

RUN mvn clean install

FROM openjdk:21-jdk

COPY --from=build src/target/blog_pg-0.0.1-SNAPSHOT.jar /run/blog_pg-0.0.1-SNAPSHOT.jar

EXPOSE 12000

ENV JAVA_OPTIONS="-Xmx2048m -Xms256m"
ENTRYPOINT java -jar $JAVA_OPTIONS /run/blog_pg-0.0.1-SNAPSHOT.jar
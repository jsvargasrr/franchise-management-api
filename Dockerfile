FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package \
    && cp target/franchise-management-api-*.jar /workspace/runtime.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/runtime.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

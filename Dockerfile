FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S bank && adduser -S bank -G bank
COPY --from=build /workspace/target/*.jar app.jar
USER bank
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

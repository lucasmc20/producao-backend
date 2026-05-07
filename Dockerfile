# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src/ src/
RUN mvn package -DskipTests -q

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S producao && adduser -S producao -G producao

COPY --from=build /app/target/producao-api-*.jar app.jar

RUN chown producao:producao app.jar
USER producao

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

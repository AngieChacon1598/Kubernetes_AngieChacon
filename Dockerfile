FROM maven:3.9.0-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
# Instalar wget para el healthcheck
RUN apk add --no-cache wget
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
# EXPOSE es solo documentación, el puerto real se configura vía variable PORT
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]

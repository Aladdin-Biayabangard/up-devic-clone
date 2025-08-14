FROM gradle:8.10.1-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Render və Railway üçün PORT env variable istifadə olunur
ENV PORT=8080

EXPOSE 8080

# Spring Boot avtomatik PORT-dan oxuyacaq (application.yml-də)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]

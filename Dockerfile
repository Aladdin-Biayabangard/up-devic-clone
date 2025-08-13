# 1. Build mərhələsi
FROM gradle:8.10.1-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# 2. Run mərhələsi
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

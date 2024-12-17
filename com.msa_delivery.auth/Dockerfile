FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app
COPY . .
# gradle 권한 설정 및 캐시 정리
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
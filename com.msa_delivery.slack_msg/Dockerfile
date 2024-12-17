# 빌드 스테이지
FROM eclipse-temurin:17 AS builder
WORKDIR /app

# 먼저 Gradle 파일들만 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Gradle 실행 권한 설정
RUN chmod +x gradlew

# 의존성 다운로드를 위한 초기 빌드
RUN ./gradlew dependencies

# 나머지 소스 코드 복사
COPY src src

# 최종 빌드
RUN ./gradlew bootJar

# 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
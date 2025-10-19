# syntax=docker/dockerfile:1.7

ARG JAVA_VERSION=21

# --- Build stage ------------------------------------------------------------
FROM eclipse-temurin:${JAVA_VERSION}-jdk AS build
WORKDIR /app

# Gradle 캐시 살리기용: 먼저 래퍼와 메타파일만 복사
COPY gradlew ./
COPY gradle gradle
COPY settings.gradle ./
COPY build.gradle ./

RUN chmod +x ./gradlew
# 의존성만 먼저 받아 캐시 생성
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew --no-daemon clean bootJar

# --- Run stage --------------------------------------------------------------
FROM eclipse-temurin:${JAVA_VERSION}-jre
ENV TZ=Asia/Seoul \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul"

WORKDIR /opt/app
# 빌드 산출물 복사
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /opt/app/app.jar"]
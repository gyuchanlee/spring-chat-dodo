# 빌드 스테이지: Java 21 사용
FROM eclipse-temurin:21-jdk AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle 래퍼에 실행 권한 부여
RUN chmod +x ./gradlew

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (Java 21 호환성 유지)
RUN ./gradlew bootJar --no-daemon

# 실행 스테이지: Java 21 런타임만 사용하여 이미지 크기 최소화
FROM eclipse-temurin:21-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출 (HTTP 및 WebSocket 모두 같은 포트 사용)
EXPOSE 8080

# 실행 명령 (JVM 옵션 최적화)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
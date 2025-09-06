FROM bellsoft/liberica-openjdk-alpine:17

# 빌드 ARG로 GitHub 시크릿 전달
ARG SERVICE_ACCOUNT_KEY_JSON

WORKDIR /app

# 서비스 계정 키 파일 생성
RUN echo "$SERVICE_ACCOUNT_KEY_JSON" > /app/service-account-key.json

# 환경 변수 설정
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/service-account-key.json

# JAR 파일 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

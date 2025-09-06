package com.silver.domain.snack.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class VertexAiConfig {

    @Value("${gemini.project-id}")
    private String projectId;

    @Value("${gemini.location}")
    private String location;

    @Value("${gemini.model}")
    private String modelName;

    @Value("${gemini.key-file}")
    private String keyFileName;

    @Bean(destroyMethod = "close")
    public VertexAI vertexAI() throws IOException {
        String keyFilePath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (keyFilePath == null) {
            throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS 환경변수가 설정되지 않았습니다.");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyFilePath))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

        return new VertexAI(projectId, location);
    }

    // 이 부분은 수정할 필요 없습니다.
    @Bean
    public GenerativeModel generativeModel(VertexAI vertexAI) {
        return new GenerativeModel(modelName, vertexAI);
    }
}
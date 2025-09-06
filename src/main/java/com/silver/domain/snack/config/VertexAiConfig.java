package com.silver.domain.snack.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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

    // VertexAI 객체를 생성할 때 서비스 계정 키 파일을 사용하도록 수정합니다.
    @Bean(destroyMethod = "close")
    public VertexAI vertexAI() throws IOException {
        // resources 폴더에서 서비스 계정 키 파일을 읽어옵니다.
        ClassPathResource resource = new ClassPathResource(keyFileName);

        // 읽어온 파일로 GoogleCredentials 객체를 생성합니다.
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream())
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

        // 생성된 인증 정보를 사용하여 VertexAI 객체를 초기화합니다.
        return new VertexAI(projectId, location);
    }

    // 이 부분은 수정할 필요 없습니다.
    @Bean
    public GenerativeModel generativeModel(VertexAI vertexAI) {
        return new GenerativeModel(modelName, vertexAI);
    }
}
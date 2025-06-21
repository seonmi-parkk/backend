package org.example.backendproject.stompwebsocket.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class GPTService {
    private final ObjectMapper mapper;

    @Value("${GPT_API_KEY}")
    private String GPTKey;

    public String gptMessage(String message) throws Exception {
        // api 호출을 위한 본문 작성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4.1");
        requestBody.put("input", message);

        // http 요청작성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization","Bearer "+GPTKey) // 인증 헤더 삽입
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))) // 본문 삽입
                .build();

        // 요청 전송 및 응답 수신
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답을 Json으로 파싱
        JsonNode jsonNode = mapper.readTree(response.body());
        System.out.println("gpt 응답 : "+jsonNode);

        // 메세지 부분만 추출하여 반환
        String gptMessageResponse = jsonNode.get("output").get(0).get("content").get(0).get("text").asText();
        return gptMessageResponse;
    }
}

package com.team.updevic001.configuration.config.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class AiGradingService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    public AiGradeResult check(String question, String correctAnswer, String studentAnswer) {
        String prompt = """
        Sual: %s
        Müəllimin düzgün cavabı: %s
        Tələbənin cavabı: %s

        Tapşırıq:
        1. Tələbənin cavabını düzgün cavabla müqayisə et.
        2. Cavabın düzgünlüyünü müəyyən et.
        3. 0-dan 100-ə qədər bal ver. Tam uyğun cavab = 100, qismən uyğun cavab = 1–99, uyğun olmayan cavab = 0.
        4. JSON formatında cavab ver:
        {
          "isCorrect": true/false,
          "score": 0–100,
          "feedback": "qısa izah",
          "correctAnswer": "düzgün cavab"
        }
        """.formatted(question, correctAnswer, studentAnswer);

        String body = """
        {
          "model": "%s",
          "messages": [
            {"role": "user", "content": "%s"}
          ],
          "temperature": 0
        }
        """.formatted(model, prompt.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());

            JSONArray contentArray = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getJSONArray("content");

            String content = contentArray
                    .getJSONObject(0)
                    .getString("text")
                    .trim();

            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)```json|```", "").trim();
            }

            return new ObjectMapper().readValue(content, AiGradeResult.class);
        } catch (Exception e) {
            throw new RuntimeException("AI cavabını əldə etmək mümkün olmadı", e);
        }
    }
}

package com.team.updevic001.configuration.config.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

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
                Question: 
                """ + question + """
                
                Teacher’s correct answer: 
                """ + correctAnswer + """
                
                Student’s answer: 
                """ + studentAnswer + """
                
                Task:
                Compare the student’s answer with the correct answer and provide feedback as if you are speaking directly to the student. Start with “You said …”.
                Determine the correctness of the answer.
                Assign a score from 0 to 100. A fully correct answer = 100, a partially correct answer = 1–99. If the answer is above 85, set correct to true, otherwise false. An incorrect answer = 0.
                Respond in JSON format:
                {
                  "correct": true/false,
                  "score": 0–100,
                  "feedback": "qısa izah",
                  "correctAnswer": "düzgün cavab"
                }
                """;


        try {
            // OpenAI üçün düzgün JSON qurmaq (Jackson ilə təhlükəsiz)
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = Map.of(
                    "model", model,
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "temperature", 0
            );

            String body = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() != 200) {
                throw new RuntimeException("OpenAI API qeyri-200 cavab qaytardı: " + response.statusCode()
                        + " | Body: " + response.body());
            }

            JSONObject json = new JSONObject(response.body());

            if (!json.has("choices")) {
                throw new RuntimeException("OpenAI cavabı `choices` daxil etmir! Cavab: " + response.body());
            }

            String content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)```json|```", "").trim();
            }

            return mapper.readValue(content, AiGradeResult.class);

        } catch (Exception e) {
            throw new RuntimeException("AI cavabını əldə etmək mümkün olmadı: " + e.getMessage(), e);
        }
    }


}

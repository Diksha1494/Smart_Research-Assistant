
package com.research.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.endpoint}")
    private String geminiApiEndpoint;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ResearchService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public String processContent(ResearchRequest request) {

        String prompt = buildPrompt(request);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                });
        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(geminiApiEndpoint)
                            .queryParam("key", geminiApiKey)
                            .build())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTextFromResponse(response);
        }

        catch (WebClientResponseException.TooManyRequests e) {

            return "⚠️ AI request limit exceeded. Please wait a minute and try again.";

        } catch (WebClientResponseException e) {
            return "AI service error: " + e.getStatusCode();

        } catch (Exception e) {
            return "Unexpected server error: " + e.getMessage();
        }

    }

    private String buildPrompt(ResearchRequest request) {

        if (request.getOperation() == null || request.getOperation().isBlank()) {
            throw new IllegalArgumentException("operation is required");
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("content is required");
        }

        StringBuilder prompt = new StringBuilder();

        switch (request.getOperation().toLowerCase()) {
            case "summarize":
                prompt.append("Provide a clear and concise summary:\n\n");
                break;

            case "suggest":
                prompt.append("Suggest related topics and further reading:\n\n");
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid operation: " + request.getOperation());
        }

        prompt.append(request.getContent());
        return prompt.toString();
    }

    private String extractTextFromResponse(String response) {
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);

            if (geminiResponse.getCandidates() != null &&
                    !geminiResponse.getCandidates().isEmpty()) {

                var content = geminiResponse.getCandidates().get(0).getContent();

                if (content != null &&
                        content.getParts() != null &&
                        !content.getParts().isEmpty()) {

                    return content.getParts().get(0).getText();
                }
            }
            return "No content found in Gemini response";

        } catch (Exception e) {
            return "Error parsing Gemini response: " + e.getMessage();
        }
    }
}

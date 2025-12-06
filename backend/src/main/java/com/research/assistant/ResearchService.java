//package com.research.assistant;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//
//
//import java.util.Map;
//
//@Service
//public class ResearchService {
//    @Value("${gemini.api.url}")
//    private String geminiApiUrl;
//
//    @Value("${gemini.api.endpoint}")
//    private String geminiApiEndpoint;
//
//    @Value("${gemini.api.key}")
//    private String geminiApiKey;
//
//    private final WebClient webClient;
//    private final ObjectMapper objectMapper;
//
//
//    public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
//        this.webClient = webClientBuilder.build();
//        this.objectMapper = objectMapper;
//    }
//
//
//    public String processContent(ResearchRequest request) {
//        // Build the prompt
//        String prompt = buildPrompt(request);
//
//        // Query the AI Model API
//        Map<String, Object> requestBody = Map.of(
//                "contents", new Object[] {
//                        Map.of("parts", new Object[]{
//                                Map.of("text", prompt)
//                        })
//                }
//        );
//
//        String response = webClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path(geminiApiEndpoint)
//                        .queryParam("key", geminiApiKey)
//                        .build())
//
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        // Parse the response
//        // Return response
//
//        return extractTextFromResponse(response);
//    }
//
//    private String extractTextFromResponse(String response) {
//        try {
//            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
//            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
//                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
//                if (firstCandidate.getContent() != null &&
//                        firstCandidate.getContent().getParts() != null &&
//                        !firstCandidate.getContent().getParts().isEmpty()) {
//                    return firstCandidate.getContent().getParts().get(0).getText();
//                }
//            }
//            return "No content found in response";
//        } catch (Exception e) {
//            return "Error Parsing: " + e.getMessage();
//        }
//    }
//
//    private String buildPrompt(ResearchRequest request) {
//        StringBuilder prompt = new StringBuilder();
//        switch (request.getOperation()) {
//            case "summarize":
//                prompt.append("Provide a clear and concise summary of the following text in a few sentences:\n\n");
//                break;
//            case "suggest":
//                prompt.append("Based on the following content: suggest related topics and further reading. Format the response with clear headings and bullet points:\n\n");
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown Operation: " + request.getOperation());
//        }
//        prompt.append(request.getContent());
//        return prompt.toString();
//    }
//}
package com.research.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.endpoint}")
    private String geminiApiEndpoint;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // ✅ Inject WebClient directly (NOT Builder)
    public ResearchService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public String processContent(ResearchRequest request) {

        String prompt = buildPrompt(request);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

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
                        "Invalid operation: " + request.getOperation()
                );
        }

        prompt.append(request.getContent());
        return prompt.toString();
    }

    private String extractTextFromResponse(String response) {
        try {
            GeminiResponse geminiResponse =
                    objectMapper.readValue(response, GeminiResponse.class);

            if (geminiResponse.getCandidates() != null &&
                    !geminiResponse.getCandidates().isEmpty()) {

                var content =
                        geminiResponse.getCandidates().get(0).getContent();

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

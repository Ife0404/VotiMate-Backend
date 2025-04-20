package com.codewiz.signupdemo.chatbot.controller;

import com.codewiz.signupdemo.chatbot.dao.ChatbotConversationRepository;
import com.codewiz.signupdemo.chatbot.dao.ChatbotLogRepository;
import com.codewiz.signupdemo.chatbot.dao.ChatbotSessionRepository;
import com.codewiz.signupdemo.chatbot.entity.ChatbotConversation;
import com.codewiz.signupdemo.chatbot.entity.ChatbotLog;
import com.codewiz.signupdemo.chatbot.entity.ChatbotSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    private final RestTemplate restTemplate;
    private final ChatbotConversationRepository conversationRepository;
    private final ChatbotSessionRepository sessionRepository;
    private final ChatbotLogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatbotController(RestTemplate restTemplate,
                             ChatbotConversationRepository conversationRepository,
                             ChatbotSessionRepository sessionRepository,
                             ChatbotLogRepository logRepository) {
        this.restTemplate = restTemplate;
        this.conversationRepository = conversationRepository;
        this.sessionRepository = sessionRepository;
        this.logRepository = logRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/message")
    public ResponseEntity<String> handleMessage(@RequestBody String message, @RequestHeader("user-id") String userId) {
        try {
            // Validate input
            if (message == null || message.trim().isEmpty()) {
                logger.warn("Received empty or null message from user: {}", userId);
                return ResponseEntity.badRequest().body("Message cannot be empty.");
            }
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Received empty or null user-id");
                return ResponseEntity.badRequest().body("User ID cannot be empty.");
            }

            // Clean message (remove surrounding quotes if present)
            String cleanedMessage = message.trim();
            if (cleanedMessage.startsWith("\"") && cleanedMessage.endsWith("\"")) {
                cleanedMessage = cleanedMessage.substring(1, cleanedMessage.length() - 1);
            }

            // Find or create a session
            String sessionId;
            Optional<ChatbotSession> existingSession = sessionRepository.findByUserId(userId)
                    .stream()
                    .filter(s -> s.getLastActive().isAfter(LocalDateTime.now().minusMinutes(30)))
                    .findFirst();
            if (existingSession.isPresent()) {
                sessionId = existingSession.get().getSessionId();
                existingSession.get().setLastActive(LocalDateTime.now());
                sessionRepository.save(existingSession.get());
                logger.info("Reusing session {} for user {}", sessionId, userId);
            } else {
                sessionId = UUID.randomUUID().toString();
                ChatbotSession session = new ChatbotSession();
                session.setSessionId(sessionId);
                session.setUserId(userId);
                session.setSlots("{}");
                session.setLastActive(LocalDateTime.now());
                sessionRepository.save(session);
                logger.info("Created new session {} for user {}", sessionId, userId);
            }

            // Prepare the request to Rasa
            String rasaUrl = "http://localhost:5005/webhooks/rest/webhook";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> requestBodyMap = Map.of("sender", userId, "message", cleanedMessage);
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            logger.debug("Sending to Rasa: {}", requestBody);

            // Call Rasa server
            String chatbotResponse = "Sorry, I couldn't process your request.";
            String intent = "unknown";
            String entities = "";
            try {
                ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
                        rasaUrl,
                        HttpMethod.POST,
                        request,
                        new ParameterizedTypeReference<List<Map<String, String>>>() {}
                );
                logger.debug("Raw Rasa response: {}", response.getBody());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                    Map<String, String> firstResponse = response.getBody().get(0);
                    chatbotResponse = firstResponse.getOrDefault("text", chatbotResponse);
                    logger.debug("Extracted chatbot response: {}", chatbotResponse);
                } else {
                    logger.warn("Empty or invalid response from Rasa: {}", response.getBody());
                }
            } catch (RestClientException e) {
                logger.error("Error calling Rasa server: {}", e.getMessage(), e);
                return ResponseEntity.status(503).body("Unable to reach the chatbot service. Please try again later.");
            }

            // Log the conversation to MySQL
            ChatbotConversation conversation = new ChatbotConversation();
            conversation.setUserId(userId);
            conversation.setTimestamp(LocalDateTime.now());
            conversation.setUserMessage(cleanedMessage);
            conversation.setChatbotResponse(chatbotResponse);
            conversation.setIntent(intent);
            conversation.setEntities(entities);
            conversation.setSessionId(sessionId);
            try {
                conversationRepository.save(conversation);
            } catch (Exception e) {
                logger.error("Error saving conversation for user {}: {}", userId, e.getMessage());
                return ResponseEntity.status(500).body("Error saving conversation. Please try again.");
            }

            return ResponseEntity.ok(chatbotResponse);
        } catch (Exception e) {
            logger.error("Unexpected error in handleMessage for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred. Please try again.");
        }
    }

    // Keep other endpoints (/log and /history) unchanged
    @PostMapping("/log")
    public ResponseEntity<Void> logConversation(@RequestBody Map<String, Object> logData) {
        try {
            if (logData == null || logData.isEmpty()) {
                logger.warn("Received empty log data");
                return ResponseEntity.badRequest().build();
            }

            ChatbotLog log = new ChatbotLog();
            log.setTimestamp(LocalDateTime.now());
            log.setUserMessage((String) logData.getOrDefault("user_message", ""));
            log.setIntent((String) logData.getOrDefault("intent", "unknown"));
            log.setIntentConfidence(Float.parseFloat(logData.getOrDefault("intent_confidence", 0.0f).toString()));
            log.setAction((String) logData.getOrDefault("action", ""));
            log.setError((String) logData.getOrDefault("error", ""));

            logRepository.save(log);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            logger.error("Error parsing intent confidence in log data: {}", e.getMessage());
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            logger.error("Error saving log: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatbotConversation>> getConversationHistory(@RequestHeader("user-id") String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Received empty or null user-id for history request");
                return ResponseEntity.badRequest().body(null);
            }

            List<ChatbotConversation> conversations = conversationRepository.findByUserId(userId);
            logger.info("Retrieved {} conversations for user {}", conversations.size(), userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            logger.error("Error retrieving conversation history for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
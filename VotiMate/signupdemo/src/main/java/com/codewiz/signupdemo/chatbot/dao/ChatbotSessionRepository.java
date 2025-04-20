package com.codewiz.signupdemo.chatbot.dao;

import com.codewiz.signupdemo.chatbot.entity.ChatbotSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotSessionRepository extends JpaRepository<ChatbotSession, String> {
    List<ChatbotSession> findByUserId(String userId);
}
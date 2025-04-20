package com.codewiz.signupdemo.chatbot.dao;

import com.codewiz.signupdemo.chatbot.entity.ChatbotConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotConversationRepository extends JpaRepository<ChatbotConversation, Long> {
    List<ChatbotConversation> findByUserId(String userId);
}
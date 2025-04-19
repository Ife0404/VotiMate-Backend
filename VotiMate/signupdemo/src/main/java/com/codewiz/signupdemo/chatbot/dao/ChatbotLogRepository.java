package com.codewiz.signupdemo.chatbot.dao;

import com.codewiz.signupdemo.chatbot.entity.ChatbotLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotLogRepository extends JpaRepository<ChatbotLog, Long> {
}
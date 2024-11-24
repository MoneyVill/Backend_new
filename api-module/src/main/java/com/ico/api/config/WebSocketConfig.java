package com.ico.api.config;

import com.ico.api.user.DbDataWebSocketHandler;
import com.ico.api.service.student.StudentService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final StudentService studentService;

    public WebSocketConfig(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new DbDataWebSocketHandler(studentService), "/ws/db-data")
                .setAllowedOrigins("*");
    }
}

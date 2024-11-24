package com.ico.api.user;

import com.ico.api.dto.student.StudentListResDto;
import com.ico.api.service.student.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class DbDataWebSocketHandler extends TextWebSocketHandler {

    private final StudentService studentService;
    private final ObjectMapper objectMapper;

    public DbDataWebSocketHandler(StudentService studentService) {
        this.studentService = studentService;
        this.objectMapper = new ObjectMapper(); // ObjectMapper 재사용을 위해 필드로 선언
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Long nationId = extractNationIdFromSession(session);

            log.info("WebSocket 연결됨: {} (nationId: {})", session.getId(), nationId);

            AtomicReference<String> lastSentData = new AtomicReference<>(""); // 마지막으로 전송한 데이터를 저장

            // 초기 데이터 전송
            try {
                List<StudentListResDto> initialStudentList = studentService.findListStudent(nationId);
                String initialJsonData = convertToJson(initialStudentList);
                session.sendMessage(new TextMessage(initialJsonData));
                lastSentData.set(initialJsonData);
            } catch (Exception e) {
                log.error("초기 데이터 전송 중 오류 발생: ", e);
            }

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (session.isOpen()) {
                            List<StudentListResDto> studentList = studentService.findListStudent(nationId);
                            String jsonData = convertToJson(studentList);

                            // 데이터가 이전에 전송한 데이터와 다를 경우에만 전송
                            if (!jsonData.equals(lastSentData.get())) {
                                session.sendMessage(new TextMessage(jsonData));
                                lastSentData.set(jsonData); // 마지막으로 전송한 데이터 업데이트
                            }
                        } else {
                            log.info("WebSocket 세션 닫힘: {}", session.getId());
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        log.error("WebSocket 데이터 전송 중 오류 발생: ", e);
                        timer.cancel();
                    }
                }
            }, 1500, 1500); // 1.5초마다 실행

        } catch (Exception e) {
            log.error("WebSocket 연결 처리 중 예외 발생: ", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 세션 닫힘: {} (상태 코드: {}, 이유: {})", session.getId(), status.getCode(), status.getReason());
    }

    private Long extractNationIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri == null) {
                throw new IllegalArgumentException("WebSocket URI가 null입니다.");
            }

            String query = uri.getQuery();
            if (query == null || !query.contains("nationId=")) {
                throw new IllegalArgumentException("nationId가 쿼리에 포함되어 있지 않습니다.");
            }

            return Long.parseLong(query.split("nationId=")[1]);
        } catch (Exception e) {
            log.error("nationId 추출 중 오류 발생: ", e);
            throw new IllegalArgumentException("유효하지 않은 nationId 값입니다.");
        }
    }

    private String convertToJson(List<StudentListResDto> studentList) {
        try {
            return objectMapper.writeValueAsString(studentList);
        } catch (Exception e) {
            log.error("JSON 변환 실패: ", e);
            return "[]";
        }
    }
}
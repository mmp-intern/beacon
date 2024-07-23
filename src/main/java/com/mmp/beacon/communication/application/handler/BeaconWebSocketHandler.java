package com.mmp.beacon.communication.application.handler;

import com.mmp.beacon.commute.application.BeaconDataProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 비콘 데이터를 처리하기 위한 WebSocket 핸들러 클래스입니다.
 * 이 클래스는 웹소켓 세션을 관리하고, 수신된 메시지를 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BeaconWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final BeaconDataProcessor beaconDataProcessor;

    /**
     * 웹소켓 연결이 성공적으로 수립된 후 호출됩니다.
     *
     * @param session 수립된 웹소켓 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("새로운 웹소켓 연결: {}", session.getId());
    }

    /**
     * 텍스트 메시지를 수신했을 때 호출됩니다.
     * 수신된 메시지를 BeaconDataProcessor를 통해 처리합니다.
     *
     * @param session 메시지를 보낸 웹소켓 세션
     * @param message 수신된 텍스트 메시지
     * @throws Exception 메시지 처리 중 발생한 예외
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        beaconDataProcessor.processBeaconData(message.getPayload());
    }

    /**
     * 웹소켓 연결이 종료된 후 호출됩니다.
     *
     * @param session 종료된 웹소켓 세션
     * @param status  연결 종료 상태
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("웹소켓 연결 종료: {}", session.getId());
    }
}

package com.mmp.beacon.global.config;

import com.mmp.beacon.communication.application.handler.BeaconWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 웹소켓 설정을 위한 구성 클래스입니다.
 * 이 클래스는 웹소켓 핸들러를 등록하고 설정합니다.
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final BeaconWebSocketHandler beaconWebSocketHandler;

    /**
     * 웹소켓 핸들러를 등록합니다.
     *
     * @param registry 웹소켓 핸들러 레지스트리
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(beaconWebSocketHandler, "/ws/beacon")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}

package com.mmp.beacon.commute.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmp.beacon.commute.application.command.BeaconData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 비콘 데이터를 처리하는 서비스 클래스입니다.
 * JSON 형식의 비콘 데이터를 파싱하여 게이트웨이와 비콘 데이터를 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BeaconDataProcessor {

    private final ObjectMapper objectMapper;
    private final CommuteService commuteService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * JSON 형식의 비콘 데이터를 파싱하여 처리합니다.
     *
     * @param jsonPayload JSON 형식의 비콘 데이터
     */
    public void processBeaconData(String jsonPayload) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonPayload);
            processGateways(rootNode);
        } catch (IOException e) {
            log.error("비콘 JSON 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("비콘 JSON 데이터 처리 중 오류 발생", e);
        }
    }

    /**
     * 루트 노드에서 게이트웨이 데이터를 처리합니다.
     *
     * @param rootNode JSON 루트 노드
     */
    private void processGateways(JsonNode rootNode) {
        rootNode.path("gateways").forEach(this::processGateway);
    }

    /**
     * 개별 게이트웨이 노드를 처리합니다.
     *
     * @param gatewayNode JSON 형식의 게이트웨이 노드
     */
    private void processGateway(JsonNode gatewayNode) {
        String gatewayMac = gatewayNode.path("gatewayMac").asText();
        List<BeaconData> beaconDataList = new ArrayList<>();

        gatewayNode.path("beacons").forEach(beaconNode -> {
            extractBeaconData(beaconNode).ifPresent(beaconDataList::add);
        });

        if (!beaconDataList.isEmpty()) {
            log.info("게이트웨이 {}에 대한 비콘 {}개 처리 중", gatewayMac, beaconDataList.size());
            commuteService.processAttendance(gatewayMac, beaconDataList);
        }
    }

    /**
     * 비콘 노드에서 비콘 데이터를 추출합니다.
     *
     * @param beaconNode JSON 형식의 비콘 노드
     * @return 추출된 비콘 데이터, 유효하지 않은 경우 빈 Optional
     */
    private Optional<BeaconData> extractBeaconData(JsonNode beaconNode) {
        String mac = beaconNode.path("mac").asText();
        String earlyTimestamp = beaconNode.path("earlyTimestamp").asText();
        String lateTimestamp = beaconNode.path("lateTimestamp").asText();

        if (mac.isEmpty() || earlyTimestamp.isEmpty() || lateTimestamp.isEmpty()) {
            log.warn("비콘 데이터가 누락되어 처리되지 않음: {}", beaconNode);
            return Optional.empty();
        }

        try {
            LocalDateTime earlyDateTime = LocalDateTime.parse(earlyTimestamp, DATE_TIME_FORMATTER);
            LocalDateTime lateDateTime = LocalDateTime.parse(lateTimestamp, DATE_TIME_FORMATTER);
            return Optional.of(new BeaconData(mac, earlyDateTime, lateDateTime));
        } catch (Exception e) {
            log.warn("Timestamp 형식 오류 : {}", beaconNode);
            return Optional.empty();
        }
    }
}

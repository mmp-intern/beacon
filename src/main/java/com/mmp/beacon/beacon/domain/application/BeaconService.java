package com.mmp.beacon.beacon.domain.application;

import com.mmp.beacon.beacon.domain.Beacon;
import com.mmp.beacon.beacon.domain.presentation.BeaconRequest;
import com.mmp.beacon.beacon.domain.presentation.BeaconResponse;
import com.mmp.beacon.beacon.domain.repository.BeaconRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeaconService {

    private final BeaconRepository beaconRepository;

    @Transactional(readOnly = true)
    public Page<BeaconResponse> getAllBeacons(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return beaconRepository.findAllByIsDeletedFalse(pageable)
                .map(this::convertToResponseDTO);
    }

    @Transactional(readOnly = true)
    public BeaconResponse getBeaconById(Long id) {
        Beacon beacon = beaconRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Beacon not found"));
        return convertToResponseDTO(beacon);
    }

    @Transactional
    public BeaconResponse createBeacon(BeaconRequest beaconRequest) {
        Beacon beacon = new Beacon(beaconRequest.getMacAddr());
        Beacon savedBeacon = beaconRepository.save(beacon);
        return convertToResponseDTO(savedBeacon);
    }

    @Transactional
    public BeaconResponse updateBeacon(Long id, BeaconRequest beaconRequest) {
        Beacon beacon = beaconRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Beacon not found"));
        beacon.updateMacAddr(beaconRequest.getMacAddr());
        Beacon updatedBeacon = beaconRepository.save(beacon);
        return convertToResponseDTO(updatedBeacon);
    }

    @Transactional
    public void deleteBeacon(Long id) {
        Beacon beacon = beaconRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Beacon not found"));
        log.info("beacon: {}", beacon);
        beacon.delete();
        beaconRepository.save(beacon);
    }

    private BeaconResponse convertToResponseDTO(Beacon beacon) {
        BeaconResponse dto = new BeaconResponse();
        dto.setId(beacon.getId());
        dto.setMacAddr(beacon.getMacAddr());
        // User가 null일 경우 처리
        if (beacon.getUser() != null) {
            dto.setUserId(beacon.getUser().getId());
        }
        return dto;
    }
}

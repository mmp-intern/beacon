package com.mmp.beacon.beacon.application;

import com.mmp.beacon.beacon.domain.Beacon;
import com.mmp.beacon.beacon.domain.repository.BeaconRepository;
import com.mmp.beacon.beacon.presentation.request.BeaconRequest;
import com.mmp.beacon.beacon.query.response.BeaconResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            dto.setUser_Id(beacon.getUser().getId());         // User의 고유 PK (id)
            dto.setUserId(beacon.getUser().getUserId());    // User의 userId 필드
            dto.setUserName(beacon.getUser().getName());     // User의 이름
        }
        return dto;
    }
}

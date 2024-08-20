package com.mmp.beacon.beacon.domain.application;

import com.mmp.beacon.beacon.domain.Beacon;
import com.mmp.beacon.beacon.domain.repository.BeaconRepository;
import com.mmp.beacon.beacon.domain.presentation.BeaconRequest;
import com.mmp.beacon.beacon.domain.presentation.BeaconResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeaconService {

    private final BeaconRepository beaconRepository;

    @Transactional(readOnly = true)
    public List<BeaconResponse> getAllBeacons() {
        return beaconRepository.findAllByIsDeletedFalse().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
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
        beacon.markAsDeleted();
        beaconRepository.save(beacon);
    }

    private BeaconResponse convertToResponseDTO(Beacon beacon) {
        BeaconResponse dto = new BeaconResponse();
        dto.setId(beacon.getId());
        dto.setMacAddr(beacon.getMacAddr());
        // User가 null일 경우 처리
        if (beacon.getUser() != null) {
            dto.setUserId(beacon.getUser().getId());
            dto.setUserName(beacon.getUser().getName());
        }
        return dto;
    }
}

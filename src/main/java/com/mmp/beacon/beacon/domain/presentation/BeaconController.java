package com.mmp.beacon.beacon.domain.presentation;

import com.mmp.beacon.beacon.domain.application.BeaconService;
import com.mmp.beacon.beacon.domain.Beacon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beacons")
@RequiredArgsConstructor
@Validated
public class BeaconController {

    private final BeaconService beaconService;

    @GetMapping
    public ResponseEntity<Page<BeaconResponse>> getAllBeacons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(beaconService.getAllBeacons(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeaconResponse> getBeaconById(@PathVariable Long id) {
        return ResponseEntity.ok(beaconService.getBeaconById(id));
    }

    @PostMapping
    public ResponseEntity<BeaconResponse> createBeacon(@RequestBody @Validated BeaconRequest beaconRequest) {
        BeaconResponse createdBeacon = beaconService.createBeacon(beaconRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBeacon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeaconResponse> updateBeacon(@PathVariable Long id, @RequestBody @Validated BeaconRequest beaconRequest) {
        BeaconResponse updatedBeacon = beaconService.updateBeacon(id, beaconRequest);
        return ResponseEntity.ok(updatedBeacon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeacon(@PathVariable Long id) {
        beaconService.deleteBeacon(id);
        return ResponseEntity.noContent().build();
    }
}
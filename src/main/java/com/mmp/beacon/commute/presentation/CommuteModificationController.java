package com.mmp.beacon.commute.presentation;

import com.mmp.beacon.commute.application.CommuteModificationService;
import com.mmp.beacon.commute.presentation.request.CommuteModificationRequest;
import com.mmp.beacon.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/commutes")
@RequiredArgsConstructor
public class CommuteModificationController {

    private final CommuteModificationService commuteModificationService;

    @PatchMapping("/{commuteId}")
    public ResponseEntity<Void> modifyCommute(
        @PathVariable Long commuteId,
        @RequestBody CommuteModificationRequest request
    ) {
        Long userId = UserUtil.getCurrentUserId();
        commuteModificationService.modifyCommute(userId, commuteId, request);
        return ResponseEntity.ok().build();
    }
}

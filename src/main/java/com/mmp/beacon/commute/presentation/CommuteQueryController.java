package com.mmp.beacon.commute.presentation;

import com.mmp.beacon.commute.application.CommuteQueryService;
import com.mmp.beacon.commute.query.response.CommuteRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/commutes")
@RequiredArgsConstructor
public class CommuteQueryController {

    private final CommuteQueryService commuteQueryService;

    @GetMapping("/today")
    public ResponseEntity<Page<CommuteRecordResponse>> listTodayCommuteRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable
    ) {
        pageable = PageRequest.of(page, size);

        // TODO: 회원 기능 구현 후 로그인한 사용자의 ID를 가져오도록 수정
        // Long userId = SecurityUtil.getCurrentUserId();
        Long userId = 5L;

        Page<CommuteRecordResponse> commutes = commuteQueryService.findTodayCommuteRecords(userId, pageable);
        return ResponseEntity.ok(commutes);
    }
}

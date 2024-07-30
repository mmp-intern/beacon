package com.mmp.beacon.commute.presentation;

import com.mmp.beacon.commute.application.CommuteQueryService;
import com.mmp.beacon.commute.application.command.CommuteSearchCommand;
import com.mmp.beacon.commute.application.command.CommuteStatisticsCommand;
import com.mmp.beacon.commute.query.response.CommuteRecordResponse;
import com.mmp.beacon.commute.query.response.CommuteStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/commutes")
@RequiredArgsConstructor
public class CommuteQueryController {

    private final CommuteQueryService commuteQueryService;

/*    @GetMapping("/today")
    public ResponseEntity<Page<CommuteRecordResponse>> listTodayCommuteRecords(Pageable pageable) {
        // TODO: 회원 기능 구현 후 로그인한 사용자의 ID를 가져오도록 수정
        // Long userId = SecurityUtil.getCurrentUserId();
        Long userId = 5L;

        Page<CommuteRecordResponse> commutes = commuteQueryService.findTodayCommuteRecords(userId, pageable);
        return ResponseEntity.ok(commutes);
    }*/

    // TODO: 특정 날짜가 아닌 특정 기간 동안의 출퇴근 기록을 조회하는 API로 수정
    @GetMapping("/daliy")
    public ResponseEntity<Page<CommuteRecordResponse>> listCommuteRecordsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "id") String searchBy,
            Pageable pageable
    ) {
        // TODO: 회원 기능 구현 후 로그인한 사용자의 ID를 가져오도록 수정
        // Long userId = SecurityUtil.getCurrentUserId();
        Long userId = 5L;

        CommuteSearchCommand command = new CommuteSearchCommand(userId, date, searchTerm, searchBy, pageable);
        Page<CommuteRecordResponse> commutes = commuteQueryService.findCommuteRecordsByDate(command);
        return ResponseEntity.ok(commutes);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Page<CommuteStatisticsResponse>> listCommuteStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "id") String searchBy,
            Pageable pageable
    ) {
        // TODO: 회원 기능 구현 후 로그인한 사용자의 ID를 가져오도록 수정
        // Long userId = SecurityUtil.getCurrentUserId();
        Long userId = 5L;

        CommuteStatisticsCommand command = new CommuteStatisticsCommand(userId, startDate, endDate, searchTerm, searchBy, pageable);
        Page<CommuteStatisticsResponse> statistics = commuteQueryService.findCommuteStatistics(command);
        return ResponseEntity.ok(statistics);
    }
}

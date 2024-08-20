package com.mmp.beacon.commute.presentation;

import com.mmp.beacon.commute.application.CommuteQueryService;
import com.mmp.beacon.commute.application.command.CommuteDailyCommand;
import com.mmp.beacon.commute.application.command.CommutePeriodCommand;
import com.mmp.beacon.commute.query.response.CommuteRecordResponse;
import com.mmp.beacon.commute.query.response.CommuteStatisticsResponse;
import com.mmp.beacon.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/commutes")
@RequiredArgsConstructor
public class CommuteQueryController {

    private final CommuteQueryService commuteQueryService;

    @GetMapping("/daliy")
    public ResponseEntity<Page<CommuteRecordResponse>> listCommuteRecordsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "id") String searchBy,
            Pageable pageable
    ) {
        Long userId = UserUtil.getCurrentUserId();
        CommuteDailyCommand command = new CommuteDailyCommand(userId, date, searchTerm, searchBy, pageable);
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
        Long userId = UserUtil.getCurrentUserId();
        CommutePeriodCommand command = new CommutePeriodCommand(userId, startDate, endDate, searchTerm, searchBy, pageable);
        Page<CommuteStatisticsResponse> statistics = commuteQueryService.findCommuteStatistics(command);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{commuteId}")
    public ResponseEntity<CommuteRecordResponse> detailCommute(
            @PathVariable Long commuteId
    ) {
        Long userId = UserUtil.getCurrentUserId();
        CommuteRecordResponse commute = commuteQueryService.findCommuteRecord(userId, commuteId);
        return ResponseEntity.ok(commute);
    }

    /*    @GetMapping("/records")
    public ResponseEntity<Page<CommuteRecordResponse>> listCommuteRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "id") String searchBy,
            Pageable pageable
    ) {
        Long userId = UserUtil.getCurrentUserId();
        CommutePeriodCommand command = new CommutePeriodCommand(userId, startDate, endDate, searchTerm, searchBy, pageable);
        Page<CommuteRecordResponse> commutes = commuteQueryService.findCommuteRecords(command);
        return ResponseEntity.ok(commutes);
    }*/


    /*    @GetMapping("/today")
    public ResponseEntity<Page<CommuteRecordResponse>> listTodayCommuteRecords(Pageable pageable) {
        Long userId = UserUtil.getCurrentUserId();
        Page<CommuteRecordResponse> commutes = commuteQueryService.findTodayCommuteRecords(userId, pageable);
        return ResponseEntity.ok(commutes);
    }*/
}

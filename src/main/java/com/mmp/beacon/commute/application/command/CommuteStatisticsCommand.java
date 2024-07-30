package com.mmp.beacon.commute.application.command;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public record CommuteStatisticsCommand(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        String searchTerm,
        String searchBy,
        Pageable pageable
) {
}

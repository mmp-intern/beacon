package com.mmp.beacon.commute.application.command;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public record CommuteSearchCommand(
        Long userId,
        LocalDate date,
        String searchTerm,
        String searchBy,
        Pageable pageable
) {
}

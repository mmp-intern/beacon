package com.mmp.beacon.commute.application.command;

import java.time.LocalDateTime;

public record BeaconData(
        String mac,
        LocalDateTime earlyTimestamp,
        LocalDateTime lateTimestamp
) {
}

package com.mmp.beacon.beacon.presentation.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class BeaconRequest {

    @NotBlank(message = "MAC 주소는 필수입니다.")
    private String macAddr;

}
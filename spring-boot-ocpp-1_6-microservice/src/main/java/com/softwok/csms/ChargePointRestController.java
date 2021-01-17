package com.softwok.csms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cp")
@RequiredArgsConstructor
public class ChargePointRestController {
    private final ChargePoint chargePoint;

    @GetMapping("/notify-boot")
    public ResponseEntity<Void> notifyBoot() {
        try {
            chargePoint.sendBootNotification();
        } catch (Exception e) {
            log.error("notifyBoot failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}

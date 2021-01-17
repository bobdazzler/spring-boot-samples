package com.softwok.csms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/csms")
@RequiredArgsConstructor
public class CsmsRestController {
    private final CsmsServer csmsServer;

    @GetMapping("/clear-cache-requested")
    public ResponseEntity<Void> clearCacheRequested() {
        try {
            csmsServer.sendClearCacheRequest(UUID.randomUUID().toString(), "cp101");
        } catch (Exception e) {
            log.error("startTransaction failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}

package com.softwok.sbscgm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionRestController {
    @GetMapping("/roles")
    public Flux<String> getRoles(Authentication authentication) {
        return Flux.fromStream(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority));
    }
}

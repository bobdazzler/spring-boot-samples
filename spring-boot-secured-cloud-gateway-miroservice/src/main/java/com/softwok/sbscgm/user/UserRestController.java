package com.softwok.sbscgm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway-microservice/users")
public class UserRestController {
    private final UserRepository userRepository;

    @PostMapping
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{emailId}")
    public Mono<User> readUser(@PathVariable("emailId") String emailId, Authentication authentication) {
        return userRepository.findById(Sha512DigestUtils.shaHex(emailId));
    }
}

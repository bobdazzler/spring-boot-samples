package com.softwok.sbscgm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class CustomJwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        String email = jwt.getClaim("email");
        log.info("email: {}", email);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (email.endsWith("gmail.com")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }
}

package com.softwok.sbscgm.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Slf4j
public class ProductAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        return mono.map(authentication -> {
            ServerHttpRequest httpServerRequest = authorizationContext.getExchange().getRequest();
            log.info("authorities: {} params: {}", authentication.getAuthorities(),
                    httpServerRequest.getQueryParams());
            return new AuthorizationDecision(true);
        });
    }
}

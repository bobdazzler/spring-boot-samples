package com.softwok.sbscgm;

import com.softwok.sbscgm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomReactiveOAuth2UserService implements ReactiveOAuth2UserService<OidcUserRequest, OidcUser> {
    private final UserRepository userRepository;

    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        OidcIdToken idToken = oidcUserRequest.getIdToken();
        String email = idToken.getClaimAsString("email");
        if (!email.endsWith("gmail.com")) {
            log.error("authorization failed for email: {}", email);
            return Mono.empty();
        }
        if (email.equals("admin@gmail.com")) {
            return Mono.just(
                    new DefaultOidcUser(
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")), idToken));
        }
        String emailIdSha = Sha512DigestUtils.shaHex(email);
        return userRepository.findById(emailIdSha).flatMap(user ->
                Mono.just(new DefaultOidcUser(user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList()), idToken)));
    }
}

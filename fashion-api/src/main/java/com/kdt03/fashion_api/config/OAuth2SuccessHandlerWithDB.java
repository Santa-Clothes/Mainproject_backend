package com.kdt03.fashion_api.config;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.repository.MemberRepository;
import com.kdt03.fashion_api.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component("OAuth2SuccessHandlerWithDB")
@RequiredArgsConstructor
public class OAuth2SuccessHandlerWithDB extends OAuth2SuccessHandler {
    private final MemberRepository memRepo;
    private final PasswordEncoder encoder;
    private final JWTUtil jwtUtil;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Map<String, String> map = getUserInfo(authentication);
        String username = map.get("provider") + "_" + map.get("id");

        if (!memRepo.existsById(username)) {
            memRepo.save(Member.builder().id(username)
                    .password(encoder.encode(java.util.UUID.randomUUID().toString()))
                    .nickname(map.get("name"))
                    .provider(map.get("provider"))
                    .profile(map.get("profile"))
                    .build());
        }
        String token = jwtUtil.getJWT(username); // JWT 생성
        sendJWTtoClient(response, token);

        String targetUrl = determineTargetUrl(request, response, token);

        if (response.isCommitted()) {
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, String token) {
        java.util.Optional<String> redirectUri = com.kdt03.fashion_api.util.CookieUtils
                .getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(jakarta.servlet.http.Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        return org.springframework.web.util.UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}

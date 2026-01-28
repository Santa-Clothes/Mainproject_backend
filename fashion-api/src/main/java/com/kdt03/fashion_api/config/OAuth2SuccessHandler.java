package com.kdt03.fashion_api.config;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    // OAuth2 인증정보에서필요한정보(provider, email)을추출하는메서드
    @SuppressWarnings("unchecked")
    Map<String, String> getUserInfo(Authentication authentication) {
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        String provider = oAuth2Token.getAuthorizedClientRegistrationId();
        System.out.println("[OAuth2SuccessHandler]Provider: " + provider);
        OAuth2User user = (OAuth2User) oAuth2Token.getPrincipal();
        String email = "unknown";
        if (provider.equalsIgnoreCase("naver")) { // naver
            email = (String) ((Map<String, Object>) user.getAttribute("response")).get("email");
        } else if (provider.equalsIgnoreCase("google")) { // google
            email = (String) user.getAttributes().get("email");
        } 
        System.out.println("[OAuth2SuccessHandler]email: " + email);
        return Map.of("provider", provider, "email", email);
    }

    void sendJWTtoClient(HttpServletResponse response, String token) {
        System.out.println("[OAuth2SuccessHandler]token:" + token);
        response.addHeader(HttpHeaders.AUTHORIZATION, token);
    }
}

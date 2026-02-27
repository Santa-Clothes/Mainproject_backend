package com.kdt03.fashion_api.config;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @SuppressWarnings("unchecked")
    Map<String, String> getUserInfo(Authentication authentication) {
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        String provider = oAuth2Token.getAuthorizedClientRegistrationId();
        OAuth2User user = (OAuth2User) oAuth2Token.getPrincipal();

        String id = "unknown";
        String name = "unknown";
        String profile = "unknown";

        if (provider.equalsIgnoreCase("naver")) {
            Map<String, Object> response = (Map<String, Object>) user.getAttribute("response");
            if (response != null) {
                id = response.get("id") != null ? response.get("id").toString() : "unknown";
                name = response.get("nickname") != null ? response.get("nickname").toString()
                        : (response.get("name") != null ? response.get("name").toString() : "unknown");
                profile = response.get("profile_image") != null ? response.get("profile_image").toString() : "no_image";
            }
        } else if (provider.equalsIgnoreCase("google")) {
            id = (String) user.getAttributes().get("sub");
            name = (String) user.getAttributes().get("name");
            profile = (String) user.getAttributes().get("picture");
        } else if (provider.equalsIgnoreCase("kakao")) {
            Map<String, Object> account = (Map<String, Object>) user.getAttributes().get("kakao_account");
            Map<String, Object> account_profile = (Map<String, Object>) account.get("profile");
            id = (String) user.getAttributes().get("id").toString();
            if (account != null && account_profile != null) {
                name = account_profile.get("nickname").toString();
                profile = account_profile.get("profile_image_url").toString();
            }
        }
        return Map.of("provider", provider, "id", id, "name", name, "profile", profile);
    }

    void sendJWTtoClient(HttpServletResponse response, String token) {
        try {
            log.debug("OAuth2 JWT token issued: {}", token);
        } catch (Exception e) {
            log.error("Failed to send JWT to client: {}", e.getMessage(), e);
        }
    }
}

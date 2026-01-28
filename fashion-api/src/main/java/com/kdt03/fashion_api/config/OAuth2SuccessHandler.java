package com.kdt03.fashion_api.config;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @SuppressWarnings("unchecked")
    Map<String, String> getUserInfo(Authentication authentication) {
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        String provider = oAuth2Token.getAuthorizedClientRegistrationId();
        OAuth2User user = (OAuth2User) oAuth2Token.getPrincipal();

        String email = "unknown";
        String name = "unknown";
        String profile = "unknown";

        if (provider.equalsIgnoreCase("naver")) {
            Map<String, Object> response = (Map<String, Object>) user.getAttribute("response");
            System.out.println("Naver response: " + response);
            if (response != null) {
                email = response.get("email") != null ? response.get("email").toString() : "unknown";
                name = response.get("nickname") != null ? response.get("nickname").toString() : 
                    (response.get("name") != null ? response.get("name").toString() : "unknown");
                profile = response.get("profile_image") != null ? response.get("profile_image").toString() : "";
            }
        } else if (provider.equalsIgnoreCase("google")) {
            email = (String) user.getAttributes().get("email");
            name = (String) user.getAttributes().get("name"); // 구글은 name
            profile = (String) user.getAttributes().get("picture");
        }

        return Map.of("provider", provider, "email", email, "name", name, "picture", profile);
    }

    void sendJWTtoClient(HttpServletResponse response, String token) {
        try {
            System.out.println("[OAuth2SuccessHandler]token:" + token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

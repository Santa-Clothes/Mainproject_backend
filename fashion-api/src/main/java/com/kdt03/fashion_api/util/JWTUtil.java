package com.kdt03.fashion_api.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;

@Component
public class JWTUtil {
	private static final long ACCESS_TOKEN_MSEC = 6 * 60 * (60 * 1000); // 6시간
	@Value("${jwt.secret}")
    private String JWT_KEY;

	public static final String prefix = "Bearer ";
	public static final String usernameClaim = "username";
	public static final String providerClaim = "provider";
	public static final String emailClaim = "email";

	private static String getJWTSource(String token) {
		if (token.startsWith(prefix))
			return token.replace(prefix, "");
		return token;
	}

	// username을 이용한 JWT 생성 메서드
	public String getJWT(String username) {
		String src = JWT.create()
				.withClaim(usernameClaim, username)
				.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_MSEC))
				.sign(Algorithm.HMAC256(JWT_KEY));
		return prefix + src;
	}
	
	// username, provider와 email을 이용한 JWT 생성 메서드
	public String getJWT(String username, String provider, String email) {
		String src = JWT.create()
				.withClaim(usernameClaim, username)
				.withClaim(providerClaim, provider)
				.withClaim(emailClaim, email)
				.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_MSEC))
				.sign(Algorithm.HMAC256(JWT_KEY));
		return prefix + src;
	}

	// JWT에서 cname 값을 추출
	public String getClaim(String token, String cname) {
		String tok = getJWTSource(token);
		Claim claim = JWT.require(Algorithm.HMAC256(JWT_KEY)).build()
				.verify(tok).getClaim(cname);
		if (claim.isMissing()) return null;
		return claim.asString();
	}
	
	// JWT 토큰이 유효한지 검사
	public boolean isExpired(String token) {
		String tok = getJWTSource(token);
		return JWT.require(Algorithm.HMAC256(JWT_KEY)).build()
				.verify(tok).getExpiresAt().before(new Date());
	}
}
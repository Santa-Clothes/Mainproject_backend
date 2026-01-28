package com.kdt03.fashion_api.config.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.repository.MemberRepository;
import com.kdt03.fashion_api.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;

import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepo;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (jwtToken == null || !jwtToken.startsWith(JWTUtil.prefix)) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = new JWTUtil().getClaim(jwtToken, JWTUtil.usernameClaim);
        String provider = jwtUtil.getClaim(jwtToken, JWTUtil.providerClaim);
        String email = jwtUtil.getClaim(jwtToken, JWTUtil.emailClaim);

        User user = null;
        Optional<Member> opt = memberRepo.findById(username);
        
        if (!opt.isPresent()) {
            // 데이터베이스는없지만provider, email이null이아니면
            // 데이터베이스에사용자를저장하지않는OAuth2 인증인경우
            if (provider == null || email == null) {
                System.out.println("[JWTAuthorizationFilter]not found user!");
                filterChain.doFilter(request, response);
                return;
            }
            System.out.println("[JWTAuthorizationFilter]username:" + username);
            user = new User(username, "****",
                    AuthorityUtils.createAuthorityList("ROLE_MEMBER"));
        } else {
            Member member = opt.get();
            System.out.println("[JWTAuthorizationFilter]" + member);
            user = new User(member.getId(), member.getPassword(),
                    AuthorityUtils.createAuthorityList("ROLE_MEMBER"));
        }
        // 인증객체생성
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        // SecuirtyContext에등록
        SecurityContextHolder.getContext().setAuthentication(auth);
        // SecurityFilterChain의다음필터로이동
        filterChain.doFilter(request, response);
    }
}
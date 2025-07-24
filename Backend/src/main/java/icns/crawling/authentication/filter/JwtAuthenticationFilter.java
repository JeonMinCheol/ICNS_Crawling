package icns.crawling.authentication.filter;

import icns.crawling.authentication.config.JwtService;
import icns.crawling.authentication.config.UserDetailsServiceImp;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

// 요청마다 한 번만 실행되는 JWT 인증 필터
// JWT 토큰 유효성 검증 후, 인증 정보를 SecurityContext에 설정

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsServiceImp;

    /**
     * HTTP 요청이 들어올 때마다 실행되는 필터 로직
     *
     * @param request      HTTP 요청 객체
     * @param response     HTTP 응답 객체
     * @param filterChain  필터 체인
     */
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        
        // 인증 엔드포인트(/api/auth) 요청은 JWT 필터를 적용하지 않고 다음 필터로 넘김
        if (request.getRequestURI().equals("/api/auth")) {
            filterChain.doFilter(request, response);
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }


        // 요청에서 JWT 토큰 추출
        final Optional<String> accessToken = jwtService.extractAccessToken(request);
        log.info("[doFilterInternal] token 값 유효성 체크 시작" + " 토큰 : " + accessToken);

        // 토큰이 존재하고, 아직 SecurityContext에 인증 정보가 없으며, 토큰이 유효한 경우
        if (accessToken.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtService.validateToken(accessToken.get()) ) {
            
            // 토큰에서 사용자 이메일 추출
            String email = jwtService.extractUserEmail(accessToken.get());

            // 이메일로 사용자 정보 로드 (UserDetails 구현체)
            UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(email);

            // 인증 객체 생성
            Authentication authentication = jwtService.getAuthentication(userDetails); //Authentication 객체 생성

            // SecurityContext 생성 후, 인증 정보 설정
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}
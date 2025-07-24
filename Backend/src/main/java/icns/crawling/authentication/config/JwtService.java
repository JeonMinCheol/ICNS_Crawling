package icns.crawling.authentication.config;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cglib.core.internal.Function;
import org.springframework.core.codec.Decoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

import static org.hibernate.query.sqm.tree.SqmNode.log;

// JWT 토큰의 생성, 파싱, 검증, 사용자 정보 추출 등을 처리하는 서비스 클래스

@Slf4j
@Service
public class JwtService {
    // application.yml 또는 application.properties에서 주입되는 값들
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // HTTP 요청 헤더에서 토큰을 추출할 때 사용하는 키
    private final String HEADER_KEY = "Authorization";

    // 토큰 접두어
    private static final String BEARER = "Bearer ";

    //엑세스 토큰 유효기간 1주
    private final long ACCCESS_TOKEN_VALIDITY_TIME =  604800 * 1000L; 

    /**
     * secretKey를 Base64로 인코딩하여 초기화하는 메서드
     * Spring이 이 Bean을 생성한 직후 실행됨
     */
    @PostConstruct
    protected void init() {
        log.info("secretKey Base64 인코딩시작");
        log.info("Original secretKey : " + secretKey);
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("Encoded Base64 secretKey : " + secretKey);
        log.info("secretKey 초기화 완료");
    }

    /**
     * 토큰에서 사용자 이메일(subject)을 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 특정 claim(주장)을 추출하는 메서드
     *
     * @param token          JWT 토큰
     * @param claimResolver  claim을 추출하는 함수형 인터페이스
     * @return claim 값
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * JWT 토큰에서 모든 claim을 추출
     *
     * @param token JWT 토큰
     * @return claims 객체
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /**
     * HTTP 요청에서 Authorization 헤더에 있는 JWT 액세스 토큰을 추출
     *
     * @param request HTTP 요청 객체
     * @return 토큰 문자열(Optional)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(HEADER_KEY)).filter(accessToken -> accessToken.startsWith(BEARER)).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /**
     * JWT 토큰의 만료 시간까지 남은 시간을 반환 (밀리초 단위)
     *
     * @param token JWT 토큰
     * @return 남은 시간 (밀리초)
     */
    public Long getExpireTime(String token) {
        Date expirationDate =  Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
        long now = new Date().getTime();
        return ((expirationDate.getTime() - now) % 1000) + 1;
    }

    /**
     * 사용자 이름(이메일)을 기반으로 JWT 토큰 생성
     *
     * @param username 사용자 이메일
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)

                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpiration)) // 유효기간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 키 설정
                .compact(); //토큰생성
    }

    /**
     * 인증 객체를 생성하여 SecurityContext에 등록하기 위한 메서드
     *
     * @param userDetails 사용자 정보
     * @return Spring Security의 Authentication 객체
     */
    public Authentication getAuthentication(UserDetails userDetails) {
        log.info("토큰 인증 정보 조회 시작");
        log.info("UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    /**
     * JWT 토큰이 유효한지 검증 (서명 확인 + 만료시간 확인)
     *
     * @param token JWT 토큰
     * @return 유효한 경우 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            log.info("토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}

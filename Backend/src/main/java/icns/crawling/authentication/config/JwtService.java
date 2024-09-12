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

@Slf4j
@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private  String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final String HEADER_KEY = "Authorization";

    private static final String BEARER = "Bearer ";

    private final long ACCCESS_TOKEN_VALIDITY_TIME =  604800 * 1000L; //엑세스 토큰 유효기간 1주


    @PostConstruct
    protected void init() {
        log.info("secretKey Base64 인코딩시작");
        log.info("Original secretKey : " + secretKey);
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("Encoded Base64 secretKey : " + secretKey);
        log.info("secretKey 초기화 완료");
    }

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(HEADER_KEY)).filter(accessToken -> accessToken.startsWith(BEARER)).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Long getExpireTime(String token) {
        Date expirationDate =  Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
        long now = new Date().getTime();
        return ((expirationDate.getTime() - now) % 1000) + 1;
    }

    public String generateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)

                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact(); //토큰생성
    }


    public Authentication getAuthentication(UserDetails userDetails) {
        log.info("토큰 인증 정보 조회 시작");
        log.info("UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    public boolean validateToken(String token) { //토큰 발리데이션
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
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            log.info("JWT 토큰이 쪼매 이상합니다.");
        }
        return false;
    }
}

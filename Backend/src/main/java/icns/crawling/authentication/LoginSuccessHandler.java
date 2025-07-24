package icns.crawling.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import icns.crawling.authentication.config.JwtService;
import icns.crawling.dto.AuthenticationResponse;
import icns.crawling.model.MemberDTO;
import icns.crawling.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 로그인 성공 시 JWT 토큰을 생성하여 응답에 포함하는 커스텀 성공 핸들러

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepo memberRepository;

    /**
     * 로그인 성공 후 처리 메서드
     * - 인증된 사용자의 이메일로 JWT 토큰 생성
     * - 응답 헤더에 Authorization 필드로 토큰 포함
     * - 응답 상태를 200 OK로 설정
     *
     * @param request         클라이언트 요청 객체
     * @param response        응답 객체
     * @param authentication  인증 객체 (로그인 성공한 사용자 정보 포함)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 인증 객체에서 사용자 이메일(아이디) 추출
        String email = extractUsername(authentication);

        // JWT 토큰 생성
        String jwtToken = jwtService.generateToken(email);

        // 응답 상태 및 헤더 설정
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Authorization", jwtToken);
    }

    /**
     * Authentication 객체에서 username(이메일)을 추출하는 유틸 메서드
     *
     * @param authentication 인증 객체
     * @return 사용자 이메일
     */
    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
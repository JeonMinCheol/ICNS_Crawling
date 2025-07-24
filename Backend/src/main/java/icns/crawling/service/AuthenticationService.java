package icns.crawling.service;

import icns.crawling.authentication.config.JwtService;
import icns.crawling.dto.AuthenticationRequest;
import icns.crawling.dto.AuthenticationResponse;
import icns.crawling.dto.RegisterRequestDTO;
import icns.crawling.model.MemberDTO;
import icns.crawling.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepo user;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리 메서드
     * 사용자의 이메일, 패스워드, 역할 정보를 기반으로 사용자 등록
     * 등록 후 JWT 토큰을 생성하여 응답
     */
    public AuthenticationResponse register(RegisterRequestDTO request) {
        // 회원 정보 생성 및 비밀번호 암호화
        var member = MemberDTO.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        // DB에 사용자 저장
        var savedUser = user.save(member);

        // JWT 토큰 생성
        var jwtToken = jwtService.generateToken(request.getEmail());

        // 응답 객체 반환
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    /**
     * 로그인 인증 처리 메서드
     * 사용자 정보를 검증하고 JWT 토큰을 발급
     */
    public AuthenticationResponse authentication(AuthenticationRequest request) throws Exception {
        // Spring Security를 통한 인증 처리
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 사용자 정보 조회 (없는 경우 예외 발생)
        MemberDTO member = user.findByEmail(request.getEmail()).orElseThrow(Exception::new);

        // JWT 토큰 생성
        String token = jwtService.generateToken(request.getEmail());

        // 응답 객체 반환
        return AuthenticationResponse.builder()
                .accessToken(token)
                .build();
    }

    /**
     * 이메일 유효성 검증 메서드
     * - 현재 로그인한 사용자가 본인 이메일을 입력했는지 확인
     * - 이메일 형식이 @gmail.com 인지 검증
     * - 중복 이메일인지 확인
     */
    public void validateEmail(Principal principal, String email) throws Exception {

        if (principal != null) {
            String currentUserEmail = user.findByEmail(principal.getName()).orElseThrow(Exception::new).getEmail();
            if (currentUserEmail.equals(email)) {
                return;
            }
        }

        if (!Pattern.matches("^[A-Za-z0-9._%+-]+@gmail\\.com\\$", email)) {
            throw new Exception();
        }

        Optional<MemberDTO> member = user.findByEmail(email);
        if (member.isPresent()) {
            throw new Exception();
        }
    }

}

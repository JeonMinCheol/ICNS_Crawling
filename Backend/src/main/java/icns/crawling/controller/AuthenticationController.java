package icns.crawling.controller;

import icns.crawling.dto.AuthenticationRequest;
import icns.crawling.dto.AuthenticationResponse;
import icns.crawling.dto.RegisterRequestDTO;
import icns.crawling.model.CaseInformationDTO;
import icns.crawling.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

// 인증 관련 API 요청을 처리하는 REST 컨트롤러
// URL 경로는 "/api/auth"로 시작하며, 로그인, 회원가입, 이메일 검증 기능 제공

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * 로그인(인증) 요청을 처리하는 엔드포인트
     *
     * @param authenticationRequest 로그인 요청 DTO (email, password 포함)
     * @return 성공 시 JWT 토큰 포함 응답, 실패 시 400 Bad Request
     */
    @PostMapping("/")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try{
            return new ResponseEntity<>(authenticationService.authentication(authenticationRequest), HttpStatus.OK);
        } catch(Exception e) {
            log.info(String.valueOf(e));
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 이메일 유효성 및 중복 여부를 검증하는 엔드포인트
     * 현재 로그인한 사용자와 같은 이메일이면 통과
     * gmail.com 도메인인지, 중복 여부도 검사
     *
     * @param principal 현재 로그인된 사용자 정보 (JWT에서 추출됨)
     * @param email     클라이언트에서 검사하고자 하는 이메일
     * @return 유효하면 200 OK, 유효하지 않으면 Exception 처리됨
     */
    @GetMapping("/validate-email/{email}")
    public ResponseEntity<Void> validateEmail(Principal principal, @PathVariable String email) throws Exception {
        authenticationService.validateEmail(principal, email);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원가입 요청을 처리하는 엔드포인트
     *
     * @param request 회원가입 요청 DTO (email, password, role 포함)
     * @return JWT 토큰 포함 응답
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

}
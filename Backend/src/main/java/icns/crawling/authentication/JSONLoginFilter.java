package icns.crawling.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// JSON 기반 로그인 요청을 처리하는 커스텀 Spring Security 인증 필터
public class JSONLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "principal";
    private static final String PASSWORD_KEY = "credential";

    // "/login" 경로로 들어온 POST 요청만 필터링 (기본 경로 설정)
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/login", "POST");

    /**
     * 생성자 - "/login" + POST 요청에만 반응하도록 설정
     */
    public JSONLoginFilter() {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER); // 위에서 설정한 "login" + POST로 온 요청을 처리하기 위해 설정
    }

    /**
     * 인증 시도 메서드: 클라이언트가 보낸 JSON 요청 본문에서 principal(이메일), credential(비밀번호)를 추출하여 인증을 시도함
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 실패 시 발생
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // 요청의 Content-Type이 application/json이 아니면 예외 발생
        if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)  ) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        // 요청 본문(JSON)을 문자열로 읽어오기
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(messageBody.toString());

        // JSON 문자열을 Map으로 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        // JSON에서 이메일(principal)과 비밀번호(credential) 추출
        String email = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);
        System.out.println(email + password);

        // 스프링 시큐리티 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);//principal과 credentials 전달

        // AuthenticationManager에게 인증 위임
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}
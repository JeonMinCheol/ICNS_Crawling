package icns.crawling.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증이 되지 않은 사용자가 보호된 리소스에 접근하려 할 때 처리하는 클래스
// 401 Unauthorized 응답과 함께 JSON 에러 메시지를 반환

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * 인증 실패 시 실행되는 메서드
     * 예: 로그인하지 않고 인증이 필요한 API에 접근했을 경우 호출됨
     *
     * @param request        HTTP 요청 객체
     * @param response       HTTP 응답 객체
     * @param authException  인증 실패 예외 객체
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info(authException.getMessage());
        setResponse(response, "인증이 실패했습니다.");
    }

    /**
     * JSON 형식의 401 Unauthorized 응답을 설정
     *
     * @param response 응답 객체
     * @param message  클라이언트에 전달할 메시지
     * @throws IOException 입출력 예외
     */
    private void setResponse(HttpServletResponse response, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        JSONObject responseJson = new JSONObject();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        responseJson.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(responseJson));
    }
}
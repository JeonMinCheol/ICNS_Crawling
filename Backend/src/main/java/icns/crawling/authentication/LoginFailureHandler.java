package icns.crawling.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * 인증 실패 처리 메서드
     * 이메일 또는 비밀번호가 잘못된 경우 JSON 형식으로 400 Bad Request 응답을 반환
     *
     * @param request   클라이언트 요청 객체
     * @param response  응답 객체
     * @param exception 발생한 인증 예외
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // JSON 변환을 위한 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // 응답 헤더 설정
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        
        // JSON 응답 객체 생성
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "이메일 또는 비밀번호가 잘못되었음");
        
        // JSON 문자열로 변환 후 응답 본문에 작성
        response.getWriter().write(objectMapper.writeValueAsString(jsonObject));
    }
}
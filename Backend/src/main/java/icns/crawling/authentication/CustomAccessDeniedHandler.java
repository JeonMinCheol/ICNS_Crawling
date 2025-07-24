package icns.crawling.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Spring Security에서 인가(Authorization) 실패 시 실행되는 커스텀 핸들러

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 권한이 없는 사용자가 보호된 리소스에 접근할 때 호출됨
     *
     * @param request   요청 객체
     * @param response  응답 객체
     * @param accessDeniedException 발생한 인가 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setResponse(response, "권한이 없습니다.");
    }

    /**
     * 응답 객체에 커스텀 JSON 메시지를 담아 반환하는 메서드
     *
     * @param response HttpServletResponse 객체
     * @param message  사용자에게 전달할 메시지 (예: "권한이 없습니다.")
     */
    private void setResponse(HttpServletResponse response, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        JSONObject responseJson = new JSONObject();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseJson.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(responseJson));
    }
}

package icns.crawling.authentication.config;

import icns.crawling.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Spring Security 인증 구성 설정 클래스
// 인증에 필요한 AuthenticationProvider 및 PasswordEncoder 빈을 등록함

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepo userRepository;
    private final UserDetailsServiceImp userDetailsServiceImp;

    /**
     * 사용자 인증에 사용할 AuthenticationProvider 빈 등록
     * - DaoAuthenticationProvider는 UserDetailsService와 PasswordEncoder를 사용해 인증을 처리
     *
     * @return 설정된 AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceImp);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈
     * - BCrypt는 보안성이 높은 해싱 알고리즘으로 Spring Security에서 권장
     *
     * @return BCrypt 기반 패스워드 인코더
     */
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

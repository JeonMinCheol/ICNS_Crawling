package icns.crawling.authentication.config;

import icns.crawling.authentication.CustomAccessDeniedHandler;
import icns.crawling.authentication.JSONLoginFilter;
import icns.crawling.authentication.LoginFailureHandler;
import icns.crawling.authentication.LoginSuccessHandler;
import icns.crawling.authentication.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Spring Security 설정 클래스
// JSON 로그인 + JWT 인증 기반의 보안 필터 체인을 구성함

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    /**
     * 보안 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    // 기본 로그인 방식 및 CSRF 비활성화
                .httpBasic(HttpBasicConfigurer -> HttpBasicConfigurer.disable())
                .formLogin(formLogin -> formLogin.disable())
                .csrf(AbstractHttpConfigurer -> AbstractHttpConfigurer.disable()) // csrf disable

                // iframe 차단 해제 (H2 콘솔 등 사용 시 필요)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) //headerOpetaion

                // 요청 권한 설정
                .authorizeHttpRequests(
                    req -> req
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // CORS preflight 허용
                            .requestMatchers(request -> CorsUtils.isPreFlightRequest(request)).permitAll()  // CORS 프리플라이트 요청 허용
                            .requestMatchers("/api/auth/**").permitAll()  // 로그인/회원가입 API는 모두 허용
                            .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
                )
                // 세션을 사용하지 않는 Stateless 방식 설정
                .sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // CORS 설정 적용
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())

                // 예외 핸들링 (403 / 401)
                .exceptionHandling(exceptionHanding -> exceptionHanding.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .exceptionHandling(authenticationEntryPoint -> authenticationEntryPoint.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))

                // 로그인 필터 등록 (LogoutFilter 이후에 실행)
                .addFilterAfter(jSONLoginFilter(), LogoutFilter.class)

                // 인증 제공자 등록
                .authenticationProvider(authenticationProvider)

                // JWT Token 필터를 id/password 인증 필터 이전에 추가
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }

    /**
     * JSON 기반 로그인 필터 Bean 등록
     * - 로그인 성공/실패 핸들러 및 AuthenticationManager 연결
     */
    @Bean
    public JSONLoginFilter jSONLoginFilter() throws Exception {
        JSONLoginFilter jSONLoginFilter
                = new JSONLoginFilter();
        jSONLoginFilter.setAuthenticationManager(authenticationManager());
        jSONLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        jSONLoginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        return jSONLoginFilter;
    }

    /**
     * 패스워드 인코더 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 인증 매니저 Bean 등록
     * - DaoAuthenticationProvider를 기반으로 사용자 인증 수행
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    /**
     * CORS 설정
     * - 프론트엔드와의 연동을 위해 허용 도메인/헤더 지정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용 도메인 (개발 환경 기준)
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        configuration.addAllowedOriginPattern("*");

        // 모든 HTTP 메서드 허용
        configuration.addAllowedMethod("*");

        // 요청 헤더 허용
        configuration.addAllowedHeader("authorization");
        configuration.addAllowedHeader("Content-Type");

        // 응답 헤더 노출 설정
        configuration.addExposedHeader("Cache-Control");
        configuration.addExposedHeader("authorization");
        configuration.addExposedHeader("Cache-Control");
        configuration.addExposedHeader("Content-Type");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

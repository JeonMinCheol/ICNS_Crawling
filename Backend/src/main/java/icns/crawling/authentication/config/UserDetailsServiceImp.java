package icns.crawling.authentication.config;

import icns.crawling.model.MemberDTO;
import icns.crawling.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security의 인증 과정에서 사용자 정보를 로드하기 위한 구현 클래스

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepo memberRepository;

    /**
     * username (여기서는 email)을 기반으로 사용자 정보를 로드함
     *
     * @param username 사용자 이메일 (로그인 시 입력한 값)
     * @return UserDetails 객체 (Spring Security가 사용하는 사용자 정보)
     * @throws UsernameNotFoundException 사용자 정보를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        MemberDTO memberDTO = MemberDTO.builder().build();
        try {
            memberDTO = memberRepository.findByEmail(username).orElseThrow();
            log.info("사용자 이메일 정보: " + memberDTO.getEmail());
        }catch (Exception exception){
            log.info("사용자 인증 실패");
            log.info(String.valueOf(exception));
            throw exception;
        }

        return MemberDTO.builder().email(memberDTO.getEmail())
                .password(memberDTO.getPassword())
                .role(memberDTO.getRole())
                .build();
    }
}
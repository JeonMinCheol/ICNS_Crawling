package icns.crawling.authentication.config;

import icns.crawling.model.MemberDTO;
import icns.crawling.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepo memberRepository;

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
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


    public AuthenticationResponse register(RegisterRequestDTO request) {
        var member = MemberDTO.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        var savedUser = user.save(member);
        var jwtToken = jwtService.generateToken(request.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) throws Exception {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        MemberDTO member = user.findByEmail(request.getEmail()).orElseThrow(Exception::new);
        String token = jwtService.generateToken(request.getEmail());

        log.info(token);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .build();
    }

    public void validateEmail(Principal principal, String email) throws Exception {

        if (principal != null) {
            String currentUserEmail = user.findByEmail(principal.getName()).orElseThrow(Exception::new).getEmail();
            if (currentUserEmail.equals(email)) {
                return;
            }
        }

        if (!Pattern.matches("^[A-Za-z0-9._%+-]+@khu\\.ac\\.kr$", email)) {
            throw new Exception();
        }

        Optional<MemberDTO> member = user.findByEmail(email);
        if (member.isPresent()) {
            throw new Exception();
        }
    }

}

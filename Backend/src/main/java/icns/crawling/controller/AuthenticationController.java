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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class    AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("/")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try{
            return new ResponseEntity<>(authenticationService.authentication(authenticationRequest), HttpStatus.OK);
        } catch(Exception e) {
            log.info(String.valueOf(e));
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate-email/{email}")
    public ResponseEntity<Void> validateEmail(Principal principal, @PathVariable String email) throws Exception {
        authenticationService.validateEmail(principal, email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

}
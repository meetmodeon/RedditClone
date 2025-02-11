package com.radish.service;

import com.radish.dto.AuthenticationResponse;
import com.radish.dto.LoginRequest;
import com.radish.dto.RefreshTokenRequest;
import com.radish.dto.RegisterRequest;
import com.radish.exceptions.SpringRedditException;
import com.radish.model.NotificationEmail;
import com.radish.model.User;
import com.radish.model.VerificationToken;
import com.radish.repository.UserRepository;
import com.radish.repository.VerificationTokenRepository;
import com.radish.security.JwtProvider;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtProvider jwtProvider;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user=new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token=generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(),"Thank you for signing up to Spring Reddit,"+
                "please click on the below url to active your account: "+
                "http://localhost:8080/api/auth/accountverification/"+token));
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String email;

        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            email = authentication.getPrincipal().toString(); // Handle case where it's a plain string
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + email));
    }

    private void fetchUserAndEnable(VerificationToken verificationToken){
        String name = verificationToken.getUser().getUsername();
        User user= userRepository.findByEmail(name).orElseThrow(()-> new SpringRedditException("User not found with name-"+ name));
        user.setEnabled(true);
        userRepository.save(user);
    }



    private String generateVerificationToken(User user){
        String token= jwtProvider.generateToken(user.getEmail());
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token){
        Optional<VerificationToken> verificationToken=verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(()-> new SpringRedditException("Invalid Token")));

    }
    public AuthenticationResponse login(LoginRequest loginRequest){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        log.info("The data of loginrequest is {}",loginRequest);
        final UserDetails userDetails=userDetailsService.loadUserByUsername(loginRequest.getUsername());
        log.info("this userDetails user is {}",userDetails.getUsername());
        String token=jwtProvider.generateToken(userDetails.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken(token).getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.extractExpiration(token).getTime()))
                .username(loginRequest.getUsername())
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token=jwtProvider.generateToken(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.extractExpiration(token).getTime()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}

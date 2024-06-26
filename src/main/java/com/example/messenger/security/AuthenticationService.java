package com.example.messenger.security;

import com.example.messenger.token.JwtRepository;
import com.example.messenger.token.JwtService;
import com.example.messenger.token.JwtToken;
import com.example.messenger.user.IUserService;
import com.example.messenger.user.User;
import com.example.messenger.user.UserUnverified;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtRepository jwtRepository;

    public UserUnverified register(User requestUser) {
        requestUser.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        return userService.saveUnverified(requestUser);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userService.findByEmail(request.getEmail());
        JwtToken jwtToken = saveNewTokenToUser(user);

        return AuthenticationResponse.builder()
                .user(user)
                .token(jwtToken.getJwt())
                .build();
    }

    public JwtToken saveNewTokenToUser(User user){

        JwtToken jwtToken = null;
        String newToken = jwtService.generateToken(user);

        try {
            jwtToken = jwtRepository.findByUser(user).get();
            jwtToken.setJwt(newToken);
            jwtRepository.save(jwtToken);
        } catch (Exception e) {
            jwtToken = JwtToken.builder().available(true).jwt(newToken).user(user).build();
            jwtRepository.save(jwtToken);
        }
        return jwtToken;
    }
}

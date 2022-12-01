package org.univaq.collectors.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.requests.Token;
import org.univaq.collectors.repositories.CollectorsRepository;

import java.util.Optional;

@Service
public class AuthService {

    private final CollectorsRepository collectorsRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;



    public AuthService(CollectorsRepository collectorsRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.collectorsRepository = collectorsRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    //login
    public Token login(String email, String password) throws BadCredentialsException {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        // Il token conterrà l'email dell'utente
        String jwt = this.jwtUtil.generateToken(email);

        return new Token(jwt);
    }

    public Optional<CollectorEntity> register(CollectorEntity collector) {
        try {
            return Optional.of(this.collectorsRepository.save(collector));
        } catch(Exception e) {
            return Optional.empty();
        }
    }


}

package org.univaq.collectors.controllers;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.requests.Registration;
import org.univaq.collectors.models.requests.Token;
import org.univaq.collectors.models.Collector;
import org.univaq.collectors.models.requests.Login;
import org.univaq.collectors.repositories.CollectorsRepository;
import org.univaq.collectors.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final CollectorsRepository collectorsRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public AuthController(
        CollectorsRepository collectorsRepository, 
        AuthenticationManager authenticationManager, 
        JwtUtil jwtUtil
    ) {
        this.collectorsRepository = collectorsRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody Login login) {
        try {
            // se non viene lanciata un eccezione, vuol dire che le credenziali sono corrette
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
            );
        } catch(BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }

        // Il token conterrà l'email dell'utente
        String jwt = this.jwtUtil.generateToken(login.getEmail());

        return ResponseEntity.ok(new Token(jwt));
    }


    @PostMapping("/register")
    public ResponseEntity<Collector> register(@Valid @RequestBody Registration collector) {

        Collector newCollector = new Collector(null, collector.getName(), collector.getSurname(), collector.getDate(), collector.getEmail(), collector.getPassword());

        Collector savedCollector = this.collectorsRepository.save(newCollector);
        
        return ResponseEntity.ok(savedCollector);
    }

}

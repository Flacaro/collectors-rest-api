package org.univaq.collectors.controllers.Public;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.models.requests.Login;
import org.univaq.collectors.models.requests.Registration;
import org.univaq.collectors.models.requests.Token;
import org.univaq.collectors.security.AuthService;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody Login login) {
        try {
            var token = this.authService.login(login.getEmail(), login.getPassword());
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CollectorEntity> register(@Valid @RequestBody Registration collector) {

        CollectorEntity newCollector = new CollectorEntity(
                null,
                collector.getName(),
                collector.getSurname(),
                collector.getBirthday(),
                collector.getUsername(),
                collector.getEmail(),
                collector.getPassword()
        );

        // 409 --> Conflict, quindi qualche vincolo non è rispettato
        return this.authService.register(newCollector)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(409).build());

    }

}
//fare servizio che chiama repository
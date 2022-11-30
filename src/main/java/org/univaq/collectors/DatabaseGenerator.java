package org.univaq.collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.security.AuthService;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DatabaseGenerator {

    private final static Logger logger = LoggerFactory.getLogger(DatabaseGenerator.class);
    private final AuthService authService;

    public DatabaseGenerator(AuthService authService) {
        this.authService = authService;
    }

    public void initializeDatabase() {
        logger.info("\u001B[31m--------------------INIZIO GENERAZIONE--------------------\u001B[0m");

        createCollector();

        logger.info("\u001B[31m--------------------FINE GENERAZIONE----------------------\u001B[0m");
    }


    private Optional<CollectorEntity> createCollector() {
        CollectorEntity marioRossi = new CollectorEntity(
                null,
                "mario",
                "rossi",
                LocalDate.of(1990, 1, 1),
                "mario",
                "mario@rossi.com",
                "secret"
        );

        CollectorEntity chiaraBianchi = new CollectorEntity(
                null,
                "chiara",
                "bianchi",
                LocalDate.of(1990, 1, 1),
                "chiara",
                "maria@bianchi.com",
                "secret"
        );


        var chiaraBianchiOptional = authService.register(chiaraBianchi);
        chiaraBianchiOptional.ifPresent(
                cb -> logger.info("Chiara Bianchi: {}", cb)
        );

        var marioRossiOptional = authService.register(marioRossi);

        if (marioRossiOptional.isPresent()) {
            try {
                logger.info("Mario Rossi: {}", marioRossiOptional.get());
                var token = authService.login(marioRossi.getEmail(), "secret");

                // log token in rosso
                logger.info("\u001B[31mToken Mario Rossi : " + token.getToken() + "\u001B[0m");

                return marioRossiOptional;
            } catch(BadCredentialsException e) {
                logger.error("Bad credentials");
            }
        } else {
            logger.warn("Collector not created");
        }

        return Optional.empty();
    }


}

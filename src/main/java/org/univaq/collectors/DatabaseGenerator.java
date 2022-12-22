package org.univaq.collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.security.AuthService;
import org.univaq.collectors.services.CollectionService;

import java.time.LocalDate;
import java.util.List;

@Service
public class DatabaseGenerator {

    private final static Logger logger = LoggerFactory.getLogger(DatabaseGenerator.class);
    private final AuthService authService;

    public DatabaseGenerator(AuthService authService) {
        this.authService = authService;
    }

    public void initializeDatabase() {
        logger.info("\u001B[31m--------------------INIZIO GENERAZIONE--------------------\u001B[0m");

        var collectors = createCollector();

        logger.info("\u001B[31m--------------------FINE GENERAZIONE----------------------\u001B[0m");
    }


    private List<CollectorEntity> createCollector() {
        CollectorEntity marioRossi = new CollectorEntity(
                null,
                "mario",
                "rossi",
                LocalDate.of(1995, 6, 26),
                "mario",
                "mario@rossi.com",
                "secret",
                null,
                null
        );

        CollectorEntity mariaBianchi = new CollectorEntity(
                null,
                "maria",
                "bianchi",
                LocalDate.of(1998, 5, 3),
                "maria",
                "maria@bianchi.com",
                "secret",
                null,
                null
        );

        CollectorEntity danieleNeri = new CollectorEntity(
                null,
                "daniele",
                "neri",
                LocalDate.of(1997, 4, 20),
                "daniele",
                "daniele@neri.com",
                "secret",
                null,
                null
        );

        var chiaraBianchiOptional = authService.register(mariaBianchi);
        chiaraBianchiOptional.ifPresent(
                cb -> logger.info("Chiara Bianchi: {}", cb)
        );

        var danieleNeriOptional = authService.register(danieleNeri);
        danieleNeriOptional.ifPresent(
                cb -> logger.info("Daniele Neri: {}", cb)
        );

        var marioRossiOptional = authService.register(marioRossi);

        if (marioRossiOptional.isPresent()) {
            try {
                logger.info("Mario Rossi: {}", marioRossiOptional.get());
                var token = authService.login(marioRossi.getEmail(), "secret");

                // log token in rosso
                logger.info("\u001B[31mToken Mario Rossi : " + token.getToken() + "\u001B[0m");
            } catch(BadCredentialsException e) {
                logger.error("Bad credentials");
            }
        } else {
            logger.warn("Collector not created");
        }

        return List.of(marioRossi, mariaBianchi, danieleNeri);
    }




}

package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectorService;

import java.security.Principal;

@RestController
@RequestMapping("/collectors")
public class PCollectorController {

    private final CollectorService collectorService;

    public PCollectorController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }
//mostra profilo
    @GetMapping("/profile")
    public ResponseEntity<CollectorEntity> getCollector(
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        return ResponseEntity.ok(collector);
    }


}

package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile")
    public ResponseEntity<CollectorEntity> getCollector(
            Authentication authentication
    ) {
        var collector = this.collectorService.getCollectorByEmail(authentication.getName());
        return ResponseEntity.ok(collector);
    }



}

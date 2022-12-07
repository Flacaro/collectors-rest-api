package org.univaq.collectors.controllers.Private;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.UserView;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectorService;

import java.security.Principal;

@RestController
@RequestMapping("/collectors")
public class PCollectorController {

    private final CollectorService collectorService;

    private final ObjectMapper objectMapper;

    public PCollectorController(CollectorService collectorService, ObjectMapper objectMapper) {
        this.collectorService = collectorService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/profile")
    public ResponseEntity<CollectorEntity> getCollector(
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        return ResponseEntity.ok(collector);
    }

    @GetMapping(value = "/{collectorId}/collections", produces = "application/json")
    public ResponseEntity<String> getPersonalCollections(
            @PathVariable  Long collectorId,
            Authentication authentication,
            @RequestParam(required = false) String view
    ) {

        var collections = this.collectorService.getPersonalCollections(collectorId, authentication);

        try {
            // Aggiungendo il query parameter alla richiesta, ?view=private
            // si ottiene la vista privata, altrimenti la pubblica
            if ("private".equals(view)) {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Private.class).writeValueAsString(collections)
                );
            } else {
                return ResponseEntity.ok(
                        objectMapper.writerWithView(UserView.Public.class).writeValueAsString(collections)
                );
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }

    }


}

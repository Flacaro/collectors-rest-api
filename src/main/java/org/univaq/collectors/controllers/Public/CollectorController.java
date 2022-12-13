package org.univaq.collectors.controllers.Public;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.univaq.collectors.SerializeWithView;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/public")
public class CollectorController {


    private final CollectorService collectorService;
    private final SerializeWithView serializeWithView;

    public CollectorController(CollectorService collectorService, SerializeWithView  serializeWithView) {
        this.collectorService = collectorService;
        this.serializeWithView = serializeWithView;
    }


    @GetMapping(value = "/collectors", produces = "application/json")
    public ResponseEntity<String> getAllCollectors(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String view
    ) {
        try {
            var collectors = collectorService.getAllCollectors(page, size);
            var collectorsByParameters = new ArrayList<CollectorEntity>();
            if(collectors.isPresent()) {
                if (email == null && username == null) {
                    return getStringResponseEntityCollectors(view, collectors);
                } else {
                    var result = this.collectorService.getCollectorsByParameters(email, username);
                        for (CollectorEntity collector : result) {
                            if (collectors.get().contains(collector)) {
                                collectorsByParameters.add(collector);
                        }
                        return getStringResponseEntityCollectors(view, Optional.of(collectorsByParameters));
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("collectors/{collectorId}")
    public ResponseEntity<String> getCollectorById(
            @PathVariable Long collectorId,
            @RequestParam(required = false) String view
    ) {
        try {
            var collector = collectorService.getById(collectorId);
            if (collector.isEmpty()) {
                if (view != null && view.equals("private")) {
                    ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PRIVATE, collector));
                }
                ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PUBLIC, collector));
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok().build();

    }



    private ResponseEntity<String> getStringResponseEntityCollectors(@RequestParam(required = false) String view, Optional<List<CollectorEntity>> publicCollectors) throws JsonProcessingException {
        if (publicCollectors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collections found");
        }
        if (view != null && view.equals("private")) {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PRIVATE, publicCollectors.get()));
        } else {
            return ResponseEntity.ok(serializeWithView.serialize(SerializeWithView.EntityView.COLLECTOR, SerializeWithView.ViewType.PUBLIC, publicCollectors.get()));
        }
    }


}

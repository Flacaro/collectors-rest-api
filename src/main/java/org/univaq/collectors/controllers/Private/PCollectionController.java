package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/collections")
public class PCollectionController {

    private final CollectorService collectorService;
    private final CollectionService collectionService;

    public PCollectionController(CollectorService collectorService, CollectionService collectionService) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
    }



    //prendo tutte le collezioni dell'utente loggato
    @GetMapping
    public ResponseEntity<List<CollectionEntity>> getCollectorCollections(Principal principal) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        var result = this.collectionService.getCollectionsByCollectorId(collector.getId());
        return ResponseEntity.ok(result);

    }

    @PostMapping("/{collectionId}")
    public ResponseEntity<CollectionEntity> saveCollectorCollection(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody CollectionEntity collection,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        var result = this.collectionService.saveCollectorCollection(collection, collector.getId()).get();
        return ResponseEntity.ok(result);
    }
}


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
    public ResponseEntity<List<CollectionEntity>> getPersonalCollections(Principal principal) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        var result = this.collectionService.getPersonalCollections(collector.getId());
        return ResponseEntity.ok(result);

    }

    @PostMapping
    public ResponseEntity<CollectionEntity> saveCollectorCollection(
            @RequestBody CollectionEntity collection,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        var result = this.collectionService.saveCollectorCollection(collection, collector.getId());
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{collectionId}")
    public ResponseEntity<CollectionEntity> deleteCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        this.collectionService.deleteCollectorCollectionById(collector.getId(), collectionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{collectionId}")
    public ResponseEntity<CollectionEntity> updateCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody CollectionEntity collection,
            Principal principal
    ) {
        var collector = this.collectorService.getCollectorByEmail(principal.getName());
        var result = this.collectionService.updateCollectorCollectionById(collector.getId(), collectionId, collection);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}


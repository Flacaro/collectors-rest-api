package org.univaq.collectors.controllers.Private;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<CollectionEntity>> getPersonalCollections(Principal principal) { //dal tokan prendo l'utente loggato principal
        var collector = this.collectorService.getCollectorByEmail(principal.getName()); //servizio che prende collezionista dal email
        var result = this.collectionService.getPersonalCollections(collector.getId()); //collezioni mie personali tramite query
        return ResponseEntity.ok(result); //ritorna collezioni mie

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

    //lista di utenti che condivido la collezione: aggiungo utente
    @PostMapping("/{collectionId}/share")
    public ResponseEntity<CollectionEntity> shareCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody List<Long> collectorIds
    ) {
        var result = this.collectionService.shareCollection(collectorIds, collectionId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{collectionId}/unshare")
    public ResponseEntity<CollectionEntity> unshareCollectorCollectionById(
            @PathVariable("collectionId") Long collectionId,
            @RequestBody List<Long> collectorIds,
            Authentication authentication
    ) {
        var result = this.collectionService.unshareCollection(
                collectorIds,
                collectionId,
                authentication
        );
        return ResponseEntity.ok(result);
    }


}


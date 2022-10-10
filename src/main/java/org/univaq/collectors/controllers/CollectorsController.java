package org.univaq.collectors.controllers;
import java.util.List;
import java.util.Optional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univaq.collectors.models.CollectionEntity;
import org.univaq.collectors.models.CollectorEntity;
import org.univaq.collectors.services.CollectionService;
import org.univaq.collectors.services.CollectorService;


@RestController
@RequestMapping("/collectors")
public class CollectorsController {

    //private final Logger logger = LoggerFactory.getLogger(CollectorsController.class);

    private final CollectorService collectorService;
    private final CollectionService collectionService;

    public CollectorsController(CollectorService collectorService, CollectionService collectionService) {
        this.collectorService = collectorService;
        this.collectionService = collectionService;
    }

    
    @GetMapping
    public ResponseEntity<List<CollectorEntity>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page, 
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam() Optional<String> email
    ) {
        return ResponseEntity.ok(this.collectorService.getAll(page, size, email));
    }

    @GetMapping("/{collectorId}/collections")
    public ResponseEntity<List<CollectionEntity>> getCollectorCollection(@PathVariable("collectorId") Long collectorId) {
        var result = this.collectionService.getCollectionByCollectorId(collectorId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{collectorId}/collections")
    public ResponseEntity<CollectionEntity> saveCollectorCollection(
            @PathVariable("collectorId") Long collectorId,
            @RequestBody CollectionEntity collection
    ) {
        var result = this.collectionService.saveCollectorCollection(collection, collectorId);

        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{collectorId}/collections/{collectionId}")
    public ResponseEntity<CollectionEntity> getCollectorCollectionById(
            @PathVariable("collectorId") Long collectorId,
            @PathVariable("collectionId") Long collectionId
    ) {
        var result = this.collectionService.getCollectorCollectionById(collectorId, collectionId);

        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
